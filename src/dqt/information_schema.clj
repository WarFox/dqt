(ns dqt.information-schema
  (:require [camel-snake-kebab.core :as csk]
            [dqt.query-runner :as q]))

(defn column-metadata
  [table-name]
  {:select [:column-name :data-type :is-nullable]
   :from   :information_schema.columns
   :where  [:= :table-name (name table-name)]})

(defn get-columns-metadata
  "Get column metadata from information schema as table qualified map"
  [db table-name]
  (let [resultset (q/execute! db (column-metadata table-name))]
    (println resultset)
    (mapv #(update-vals % csk/->kebab-case-keyword) resultset)))
