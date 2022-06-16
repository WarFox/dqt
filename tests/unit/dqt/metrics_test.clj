(ns dqt.metrics-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [dqt.metrics :as sut]
   [dqt.sql.expressions :as expressions]))

(s/def :columns/data-type #{:integer :character-varying :date})
(s/def :columns/column-name keyword?)
(s/def :columns/is-nullable #{:yes :no})
(s/def :columns/metadata (s/keys :req [:columns/data-type :columns/column-name :columns/is-nullable]))

(deftest get-select-map-test
  (let [columns [#:columns {:data-type :integer :metrics [:avg :max] :column-name :salary}]]
    (is (= {:select [[[:avg :salary] :avg-salary] [[:max :salary] :max-salary]]}
           (sut/get-select-map columns [:avg :max])))

    (deftest include-row-count-if-needed
      (is (= {:select [[[:count :*] :row-count] [[:avg :salary] :avg-salary] [[:max :salary] :max-salary]]}
             (sut/get-select-map columns [:avg :max :row-count]))))))

(deftest enrich-column-metadata-test
  (is (= #:columns{:data-type :integer :metrics [:avg :max :min :stddev :sum :variance]}
         (sut/enrich-column-metadata #:columns{:data-type :integer}))))

(deftest get-metrics-test
  (testing "must throw error when column metadat is empty"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo #"columns-metadata is empty. Is the table name correct?"
         (sut/get-metrics :db :table-name [] :metrics)))))

(deftest metrics-functions
  (testing "avg"
    (is (= (:avg sut/metrics-fns) expressions/-avg)))

  (testing "avg-length"
    (is (= (:avg-length sut/metrics-fns) expressions/-avg-length)))

  (testing "max"
    (is (= (:max sut/metrics-fns) expressions/-max)))

  (testing "max-length"
    (is (= (:max-length sut/metrics-fns) expressions/-max-length)))

  (testing "min"
    (is (= (:min sut/metrics-fns) expressions/-min)))

  (testing "min-length"
      (is (= (:min-length sut/metrics-fns) expressions/-min-length)))

  (testing "stddev"
    (is (= (:stddev sut/metrics-fns) expressions/-stddev)))

  (testing "sum"
    (is (= (:sum sut/metrics-fns) expressions/-sum)))

  (testing "variance"
    (is (= (:variance sut/metrics-fns) expressions/-variance))))
