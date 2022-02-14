(ns dqt.query-runner-integration-test
  (:require [clojure.test :refer :all]
            [dqt.query-runner :as sut]
            [dqt.migrations :as m]
            [dqt.config :as config]))

(defn db-setup
  [f]
  (m/migrate)
  (f)
  (m/rollback))

(use-fixtures
  :once db-setup)

(deftest query-runner-test
  (is (= '({:count 40}) (sut/count* config/db :employees))))
