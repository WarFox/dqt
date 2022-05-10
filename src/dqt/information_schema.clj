(ns dqt.information-schema
  (:require [dqt.query-runner :as q]
            [honey.sql :as honey]))

(defn column-metadata
  [table-name]
  {:select [:column-name :data-type :is-nullable]
   :from   :information_schema.columns
   :where  [:= :table-name (name table-name)]})

(defn get-columns-metadata
  [table-name db]
  (->> (column-metadata table-name)
       honey/format
       (q/execute! db)))
