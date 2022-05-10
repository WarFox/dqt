(ns dqt.information-schema
  (:require [honey.sql :as honey]
            [next.jdbc :as jdbc]))

(defn column-metadata
  [table-name]
  {:select [:column-name :data-type :is-nullable]
   :from   :information_schema.columns
   :where  [:= :table-name (name table-name)]})

(defn get-columns-metadata
  [table-name db]
  (->> (column-metadata table-name)
       honey/format
       (jdbc/execute! db)))
