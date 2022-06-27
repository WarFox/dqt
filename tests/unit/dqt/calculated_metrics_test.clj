(ns dqt.calculated-metrics-test
  (:require
   [dqt.calculated-metrics :as sut]
   [clojure.test :refer :all]))

(defonce metrics {:count-my-column 20
                  :row-count 25
                  :variance-my-column 30} )

(deftest get-metric-values-from-result
  (is (= 20 (sut/values-count metrics "my-column")))
  (is (= 30 (sut/variance metrics "my-column"))))

(deftest -missing-count-test
  (is (= 5 (sut/-missing-count metrics :my-column)))
  (is (= 5 (sut/-missing-count metrics "my-column"))))

(deftest -missing-percentage-test
  (is (= 20 (sut/-missing-percentage metrics :my-column)))
  (is (= 20 (sut/-missing-percentage metrics "my-column"))))

(deftest -values-percentage-test
  (is (= 80 (sut/-values-percentage metrics :my-column)))
  (is (= 80 (sut/-values-percentage metrics "my-column"))))

(deftest values+missing
  (testing "value count + missing count percentage = 100"
    (is (= 100 (+ (sut/-values-percentage metrics :my-column)
                  (sut/-missing-percentage metrics :my-column))))))
