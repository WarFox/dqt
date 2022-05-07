(ns dqt.metrics-test
  (:require [clojure.test :refer :all]
            [dqt.metrics :as sut]
            [clojure.set :as set]))

(deftest build-select-map-test
  (is (= {:select [[[:count :column-name] :count-column-name]]}
         (sut/build-select-map [:row-count]))))

(deftest format-sql-test
  (is (= ["SELECT COUNT(column_name) AS count_column_name FROM table_name"]
         (sut/format-sql [:row-count] :table-name))))
