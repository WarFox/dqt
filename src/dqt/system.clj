(ns dqt.system
  (:require
   [dqt.checks :as c]
   [dqt.information-schema :as info]
   [dqt.metrics :as m]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]))

(defn config
  [{:keys [datastore table-name] :as options}]
  {::db-connection             datastore
   ::columns-metadata          {:db         (ig/ref ::db-connection)
                                :table-name table-name}
   ::columns-metadata-enriched {:columns (ig/ref ::columns-metadata)
                                :metrics (:metrics options)}
   ::sql-metrics               (assoc options
                                      :db (ig/ref ::db-connection)
                                      :columns-metadata (ig/ref ::columns-metadata-enriched))
   ::calculated-metrics        {:columns     (ig/ref ::columns-metadata-enriched)
                                :sql-metrics (ig/ref ::sql-metrics)}
   ::validity-checks           {:columns (ig/ref ::columns-metadata-enriched)}
   ::test-results              {:sql-metrics        (ig/ref ::sql-metrics)
                                :calculated-metrics (ig/ref ::calculated-metrics)
                                :validity-checks    (ig/ref ::validity-checks)
                                :tests              (:tests options)}
   ::report                    (ig/ref ::test-results)})

(defmethod ig/init-key ::db-connection
  [_ db]
  (jdbc/get-connection db))

(defmethod ig/init-key ::columns-metadata
  [_ {:keys [db table-name]}]
  (info/get-columns-metadata db table-name))

(defmethod ig/init-key ::columns-metadata-enriched
  [_ {:keys [columns metrics]}]
  (mapv #(m/enrich-column-metadata %  metrics) columns))

(defmethod ig/init-key ::sql-metrics
  [_ {:keys [db table-name columns-metadata]}]
  (m/get-metrics db table-name columns-metadata))

(defmethod ig/init-key ::validity-checks
  [_ {:keys [columns]}]
  (println "validity checks")
  (println columns))

(defmethod ig/init-key ::calculated-metrics
  [_ {:keys [columns sql-metrics]}]
  (into {} (map #(m/calculated-metrics % sql-metrics) columns)))

(defmethod ig/init-key ::test-results
  [_ {:keys [sql-metrics calculated-metrics tests]}]
  (let [metrics (merge sql-metrics calculated-metrics)]
    (mapv #(c/run-check % metrics) tests)))

(defn- test-failed?
  [test-result]
  (not (second test-result)))

(defmethod ig/init-key ::report
  [_ test-results]
  (if (some test-failed? test-results)
    (println "Failed")
    (println "Success"))
  test-results)

(defn init
  "Initialise system"
  [options]
  (ig/init (config options)))
