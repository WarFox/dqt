(ns dqt.metrics-test
  (:require [clojure.test :refer :all]
            [dqt.metrics :as sut]))

(deftest build-select-map-test
  (is (= {:select [[:%count.*]]}
         (sut/build-select-map [:row-count]))))

(deftest format-sql-test
  (is (= ["SELECT COUNT(*)"]
         (sut/format-sql [:row-count]))))
