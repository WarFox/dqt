(ns dqt.core
  (:gen-class)
  (:require [aero.core :as aero]
            [dqt.cli :as cli]
            [dqt.system :as system]))

(defn- read-inputs
  [{:keys [datastore table]}]
  (map aero/read-config [datastore table]))

(defn- ->options
  [parsed-options]
  (let [{:keys [action options]} parsed-options
        [datastore table]        (read-inputs options)]
    (assoc table
           :action action
           :datastore datastore)))

(defn app
  [options]
  (try
    (system/init options)
    (catch Exception ex
      (println (ex-message ex))
      (println "Caused by:")
      (println ((juxt ex-message ex-data) (ex-cause ex))))))

(defn -main
  "entrypoint"
  [& args]
  (-> args
      (cli/init)
      (->options)
      (app)))
