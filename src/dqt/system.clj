(ns dqt.system
  (:require
   [dqt.checks :as c]
   [dqt.cli :as cli]
   [dqt.metrics :as m]
   [dqt.information-schema :as info-schema]
   [integrant.core :as ig]))

(defn config
  [options]
  {::columns-metadata options
   ::sql-metrics      (assoc options
                             :columns-metadata (ig/ref ::columns-metadata))
   ::test-results     {:metrics (ig/ref ::sql-metrics)
                       :tests   options}})

(defmethod ig/init-key ::datastore
  [_ {:keys [datastore]}]
  datastore)

(defmethod ig/init-key ::action
  [_ {:keys [action]}]
  action)

(defmethod ig/init-key ::columns-metadata
  [_ {:keys [datastore table-name]}]
  (info-schema/get-columns-metadata datastore table-name))

(defmethod ig/init-key ::sql-metrics
  [_ {:keys [datastore table-name columns-metadata metrics]}]
  (when (empty? columns-metadata)
    (throw (ex-info "columns-metadata is empty. Is the table name correct?"
                    {:table-name table-name})))
  (m/get-metrics datastore table-name columns-metadata metrics))

(defmethod ig/init-key ::test-results
  [_ {:keys [metrics tests]}]
  (mapv #(c/run-check % metrics) (:tests tests)))

(defn init
  "Initialise system"
  [options]
  (ig/init (config options)))
