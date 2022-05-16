(ns dqt.metrics-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]
            [dqt.metrics :as sut]
            [clojure.set :as set]))

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
  (is (= #:columns{:data-type :integer :metrics [:avg :min :max]}
         (sut/enrich-column-metadata #:columns{:data-type :integer}))))
