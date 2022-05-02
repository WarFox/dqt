(ns dqt.core
  (:gen-class)
  (:require [aero.core :refer (read-config)]
            [dqt.cli :as cli]
            [dqt.metrics :as m]
            [dqt.query-runner :as q]
            [next.jdbc :as jdbc]))

(defn load-inputs
  [[datastore table]]
  (map read-config [datastore table]))

(defn process
  [parsed-options]
  (let [{:keys [action options]} parsed-options
        [datastore table] (load-inputs ((juxt :datastore :table) options))]
    (println (m/format-sql (:metrics table)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      cli/parse
      cli/validate-options
      cli/pass-or-exit
      process))
