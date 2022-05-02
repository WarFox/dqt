(ns dqt.fixtures
  (:require  [clojure.test :refer :all]
             [dqt.migrations :as m]))

(defn run-migrations
  "Run migrations"
  [f]
  (m/migrate)
  (f)
  (m/rollback))
