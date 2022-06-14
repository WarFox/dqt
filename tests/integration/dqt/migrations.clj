(ns dqt.migrations
  (:require
   [dqt.config :as config]
   [migratus.core :as migratus]))

(defn migrate
  [_]
  (migratus/init config/migratus)
  (migratus/migrate config/migratus))

(defn rollback
  [_]
  (migratus/rollback config/migratus))
