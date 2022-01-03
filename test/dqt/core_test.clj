(ns dqt.core-test
  (:require [clojure.test :refer :all]
            [fixtures :as fixtures]
            [dqt.core :refer :all]))

(deftest a-test
  (testing "I pass"
    (is (= 1 1))))

(use-fixtures :once fixtures/run-migrations)
