(ns dqt.migrations
  (:require
   [dqt.config :as config]
   [migratus.core :as migratus]))

(defn migrate
  []
  (migratus/init config/migratus)
  (migratus/migrate config/migratus))

(defn rollback
  []
  (migratus/rollback config/migratus))
