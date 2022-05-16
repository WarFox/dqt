(ns dqt.core
  (:gen-class)
  (:require [aero.core :as aero]
            [dqt.cli :as cli]
            [dqt.information-schema :as info-schema]
            [dqt.metrics :as m]
            [dqt.checks :as c]
            [dqt.query-runner :as q]))

(defn load-inputs
  [{:keys [datastore table]}]
  (map aero/read-config [datastore table]))

(defn process
  [parsed-options]
  (let [{:keys [action options]}                       parsed-options
        [datastore {:keys [table-name metrics tests]}] (load-inputs options)
        metrics                                        (m/get-metrics datastore table-name metrics)]
    ;; validate metrics
    (println metrics)
    (mapv #(c/run-check % metrics) tests)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      cli/parse
      cli/validate-options
      cli/pass-or-exit
      process))
