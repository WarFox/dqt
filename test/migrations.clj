(ns migrations
  (:require [migratus.core :as migratus]
            [dqt.config :as c]))

(defn migrate
  []
  (migratus/init c/config)
  (migratus/migrate c/config))

(defn rollback
  []
  (migratus/rollback c/config))
