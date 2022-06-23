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

(deftest sql-metrics-map-test
  ;; TODO add more data-types and metrics to this test, generative test?
  (let [columns [#:columns {:data-type :integer :sql-metrics [:avg :max] :column-name :salary}
                 #:columns {:data-type :date :sql-metrics [:min :max] :column-name :hire-date}]]

    (testing "row-count must be included with required metric fields"
      (is (= {:select [[[:count :*] :row-count]
                       [[:avg :salary] :avg-salary]
                       [[:max :salary] :max-salary]
                       [[:min :hire-date] :min-hire-date]
                       [[:max :hire-date] :max-hire-date]]
              :from :my-table-name}
             (sut/sql-metrics-map columns :my-table-name))))))

(deftest enrich-column-metadata-test
  (testing "add sql-metrics and calculated metrics list to columns map"
    (is (= #:columns{:data-type          :integer
                     :sql-metrics        [:values-count :avg :max]
                     :calculated-metrics '(:missing-count)}
           (sut/enrich-column-metadata #:columns{:data-type :integer} [:avg :max])))))

(deftest get-metrics-test
  (testing "must throw error when column metadat is empty"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo #"columns-metadata is empty. Is the table name correct?"
         (sut/get-metrics :db :table-name [])))))

(deftest required-metrics-test
  (let [metric-group {:integer [:avg :max]
                      :any [:values-count]}]
    (is (= [:values-count :avg]
           (sut/required-metrics metric-group :integer [:avg])))))

(deftest metrics-functions
  ;; TODO automate these test cases
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
    (is (= (:variance sut/metrics-fns) expressions/-variance)))

  (testing "-values-count"
    (is (= (:values-count sut/metrics-fns) expressions/-values-count))))

(deftest get-metric-values-from-result
  (let [metrics {:count-my-column 25
                 :variance-my-column 30}]
    (is (= 25 (sut/get-count metrics "my-column")))
    (is (= 30 (sut/get-variance metrics "my-column")))))

(deftest calculated-metrics-test
  (let [columns     {:columns/calculated-metrics [:missing-count]
                     :columns/column-name        :my-column}
        sql-metrics {:row-count 40 :count-my-column 38}]
    (is (= {:row-count 40 :count-my-column 38 :missing-count-my-column 2}
           (sut/calculated-metrics columns sql-metrics)))))

(deftest fields-test
  (let [metric-fns {:metric-1 (fn [x] (str "metric-1 of " x))
                    :metric-2 (fn [x] (str "metric-2 of " x))}]
    (is (= ["metric-1 of my-column" "metric-2 of my-column"]
           (sut/fields metric-fns
                       #:columns{:sql-metrics [:metric-1 :metric-2] :column-name "my-column"})))))
