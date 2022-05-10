(ns dqt.metrics-test
  (:require [clojure.test :refer :all]
            [dqt.metrics :as sut]
            [clojure.set :as set]))

(deftest get-select-map-test
  (is (= {:select [[[:count :column-name] :count-column-name]]}
         (sut/get-select-map [:row-count]))))

(deftest format-sql-test
  (is (= ["SELECT COUNT(column_name) AS count_column_name FROM table_name"]
         (sut/format-sql [:row-count] :table-name))))

(deftest enrich-column-metadata-test
  (is (= #:columns{:data-type :integer :metrics [:avg :min :max]}
         (sut/enrich-column-metadata #:columns{:data-type :integer}))))
