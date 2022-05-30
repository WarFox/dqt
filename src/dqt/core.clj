(ns dqt.core
  (:gen-class)
  (:require [aero.core :as aero]
            [dqt.cli :as cli]
            [dqt.system :as system]))

(defn- load-inputs
  [{:keys [datastore table]}]
  (map aero/read-config [datastore table]))

(defn- ->options
  [parsed-options]
  (let [{:keys [action options]} parsed-options
        [datastore table]        (load-inputs options)]
    (assoc table
           :action action
           :datastore datastore)))

(defn -main
  "entrypoint"
  [& args]
  (-> args
      cli/init
      ->options
      system/init))
