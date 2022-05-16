(ns dqt.checks-test
  (:require  [clojure.test :refer :all]
             [dqt.checks :as sut]))

(deftest run-check-test
  (is (= [[:metric-column '> 2] true] (sut/run-check [:metric-column '> 2] {:metric-column 20}))))
