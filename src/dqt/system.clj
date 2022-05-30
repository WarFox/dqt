(ns dqt.system
  (:require
   [dqt.checks :as c]
   [dqt.cli :as cli]
   [dqt.metrics :as m]
   [integrant.core :as ig]))

(defn config
  [options]
  {::sql-metrics  options
   ::test-results {:metrics (ig/ref ::sql-metrics)
                   :tests   options}})

(defmethod ig/init-key ::datastore
  [_ {:keys [datastore]}]
  datastore)

(defmethod ig/init-key ::action
  [_ {:keys [action]}]
  action)

(defmethod ig/init-key ::sql-metrics
  [_ {:keys [datastore table-name metrics]}]
  (m/get-metrics datastore table-name metrics))

(defmethod ig/init-key ::test-results
  [_ {:keys [metrics tests]}]
  (mapv #(c/run-check % metrics) (:tests tests)))

(defn init
  [parsed-options]
  (let [config (config parsed-options)]
    (ig/init config)))
