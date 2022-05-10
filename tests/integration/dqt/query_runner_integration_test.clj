(ns dqt.query-runner-integration-test
  (:require [clojure.test :refer :all]
            [dqt.config :as config]
            [dqt.fixtures :as f]
            [dqt.migrations :as m]
            [dqt.query-runner :as sut]))

(use-fixtures :once f/run-migrations)

(deftest query-runner-test
  (is (= '({:count 40}) (sut/execute! config/db {:select [[:%count.*]]
                                                 :from   :employees}))))
