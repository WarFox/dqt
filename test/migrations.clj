(ns migrations
  (:require [migratus.core :as migratus]
            [dqt.config :as config]))

(defn migrate
  []
  (migratus/init config/migratus)
  (migratus/migrate config/migratus))

(defn rollback
  []
  (migratus/rollback config/migratus))
