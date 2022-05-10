(ns dqt.informations-schema-test
  (:require  [clojure.test :refer :all]
             [dqt.information-schema :as sut]))

(deftest column-metadata-test
  (is (= {:select [:column-name :data-type :is-nullable]
          :from :information_schema.columns
          :where [:= :table-name "my-table-name"]}
         (sut/column-metadata :my-table-name))))
