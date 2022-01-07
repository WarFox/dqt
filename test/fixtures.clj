(ns fixtures
  (:require  [clojure.test :as t]
             [migrations :as m]))

(defn run-migrations
  "Run migrations"
  [f]
  (m/migrate)
  (f)
  (m/rollback))
