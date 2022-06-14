(ns dqt.fixtures
  (:require
   [dqt.migrations :as m]))

(defn run-migrations
  "Run migrations"
  [f]
  (m/migrate f)
  (f)
  (m/rollback f))
