(ns dqt.core
  (:gen-class)
  (:require [dqt.cli :as cli]))

(defn process
  [options]
  (println "Processing " options))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      cli/parse
      cli/validate-options
      cli/pass-or-exit
      process))
