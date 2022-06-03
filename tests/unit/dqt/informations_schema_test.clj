(ns dqt.informations-schema-test
  (:require  [clojure.test :refer :all]
             [dqt.query-runner :as q]
             [dqt.information-schema :as sut]))

(deftest column-metadata-test
  (is (= {:select [:column-name :data-type :is-nullable]
          :from :information_schema.columns
          :where [:= :table-name "my_table_name"]}
         (sut/column-metadata :my-table-name))))

(deftest get-columns-metadata-test
  (with-redefs [q/execute! (fn [db query] [#:columns {:data-type "integer" :another-column "value"}
                                           #:columns {:data-type "character varying" :another-column "SOME_value"}])]
    (is (= [#:columns {:data-type :integer :another-column :value}
            #:columns {:data-type :character-varying :another-column :some-value}]
           (sut/get-columns-metadata :db :my-table-name)))))
