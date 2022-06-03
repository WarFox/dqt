(ns dqt.checks-test
  (:require
   [clojure.test :refer :all]
   [dqt.checks :as sut]))

(deftest run-check-test
  (let [metrics {:metric-column-1 20 :metric-column-2 nil}]
    (testing "metric-value is not nil"
      (is (= [[:metric-column-1 '> 2] true]
             (sut/run-check [:metric-column-1 '> 2] metrics))))

    (testing "metric-value is nil"
      (is (= [[:metric-column-2 '> 2] nil]
             (sut/run-check [:metric-column-2 '> 2] metrics))))))
