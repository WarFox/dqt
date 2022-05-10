(ns dqt.core
  (:gen-class)
  (:require [aero.core :refer (read-config)]
            [dqt.cli :as cli]
            [dqt.information-schema :as info-schema]
            [dqt.metrics :as m]
            [dqt.query-runner :as q]))

(defn load-inputs
  [[datastore table]]
  (map read-config [datastore table]))

(defn process
  [parsed-options]
  (let [{:keys [action options]} parsed-options
        [datastore table]        (load-inputs ((juxt :datastore :table) options))
        [table-name metrics]     ((juxt :table-name :metrics) table)
        columns-metadata          (info-schema/get-columns-metadata table-name datastore)]
    ;; validate metrics
    (println (mapv m/enrich-column-metadata columns-metadata))
    (println (m/format-sql metrics table-name))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      cli/parse
      cli/validate-options
      cli/pass-or-exit
      process))
