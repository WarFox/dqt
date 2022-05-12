(ns dqt.core
  (:gen-class)
  (:require [aero.core :refer (read-config)]
            [dqt.cli :as cli]
            [dqt.information-schema :as info-schema]
            [dqt.metrics :as m]
            [dqt.query-runner :as q]))

(defn load-inputs
  [{:keys [datastore table]}]
  (map read-config [datastore table]))

(defn process
  [parsed-options]
  (let [{:keys [action options]}           parsed-options
        [datastore table]                  (load-inputs options)
        {:keys [table-name metrics tests]} table]
    ;; validate metrics
    (println tests)
    (println (m/get-metrics datastore table-name metrics))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      cli/parse
      cli/validate-options
      cli/pass-or-exit
      process))
