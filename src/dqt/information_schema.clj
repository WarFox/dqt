(ns dqt.information-schema
  (:require [camel-snake-kebab.core :as csk]
            [dqt.query-runner :as q]
            [honey.sql :as honey]))

(defn column-metadata
  [table-name]
  {:select [:column-name :data-type :is-nullable]
   :from   :information_schema.columns
   :where  [:= :table-name (name table-name)]})

(defn get-columns-metadata
  "Get column metadata from information schema as table qualified map"
  [db table-name]
  (let [resultset  (->> table-name
                        column-metadata
                        honey/format
                        (q/execute! db))]
    (mapv
     #(update-vals % csk/->kebab-case-keyword)
     resultset)))
