(ns dqt.metrics-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [dqt.metrics :as sut]))

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
    (is (= [[:avg :my-column] :avg-my-column]
           (sut/-avg :my-column))))

  (testing "avg-length"
    (is (= [[:avg [[:length :my-column]]] :avg-length-my-column]
           (sut/-avg-length :my-column))))

  (testing "max"
    (is (= [[:max :my-column] :max-my-column]
           (sut/-max :my-column))))

  (testing "max-length"
    (is (= [[:max [[:length :my-column]]] :max-length-my-column]
           (sut/-max-length :my-column))))

  (testing "min"
    (is (= [[:min :my-column] :min-my-column]
           (sut/-min :my-column))))

  (testing "min-length"
    (is (= [[:min [[:length :my-column]]] :min-length-my-column]
           (sut/-min-length :my-column))))

  (testing "stddev"
    (is (= [[:stddev :my-column] :stddev-my-column]
           (sut/-stddev :my-column))))

  (testing "sum"
    (is (= [[:sum :my-column] :sum-my-column]
           (sut/-sum :my-column))))

  (testing "variance"
    (is (= [[:variance :my-column] :variance-my-column]
           (sut/-variance :my-column)))))
