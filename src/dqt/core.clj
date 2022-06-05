(ns dqt.core
  (:gen-class)
  (:require
   [aero.core :as aero]
   [dqt.cli :as cli]
   [dqt.system :as system]))

(defn- read-inputs
  [{:keys [datasource table]}]
  (map aero/read-config [datasource table]))

(defn- merge-options
  [parsed-options]
  (let [{:keys [action options]} parsed-options
        [datasource table]        (read-inputs options)]
    (merge table
           {:action    action
            :datasource datasource})))

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
      (merge-options)
      (app)))
