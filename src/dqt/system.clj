(ns dqt.system
  (:require
   [dqt.checks :as c]
   [dqt.information-schema :as info]
   [dqt.metrics :as m]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]))

(defn config
  [{:keys [datastore table-name] :as options}]
  {::db-connection    datastore
   ::columns-metadata {:db         (ig/ref ::db-connection)
                       :table-name table-name}
   ::sql-metrics      (assoc options
                             :db (ig/ref ::db-connection)
                             :columns-metadata (ig/ref ::columns-metadata))
   ::test-results     {:metrics (ig/ref ::sql-metrics)
                       :tests   options}})

(defmethod ig/init-key ::db-connection
  [_ db]
  (jdbc/get-connection db))

(defmethod ig/init-key ::columns-metadata
  [_ {:keys [db table-name]}]
  (info/get-columns-metadata db table-name))

(defmethod ig/init-key ::sql-metrics
  [_ {:keys [db table-name columns-metadata metrics]}]
  (m/get-metrics db table-name columns-metadata metrics))

(defmethod ig/init-key ::test-results
  [_ {:keys [metrics tests]}]
  (mapv #(c/run-check % metrics) (:tests tests)))

(defn init
  "Initialise system"
  [options]
  (ig/init (config options)))
