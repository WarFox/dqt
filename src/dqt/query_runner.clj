(ns dqt.query-runner
  (:require [honey.sql :as honey]
            [honey.sql.helpers :as helpers]
            [next.jdbc :as jdbc]
            [dqt.queries :as q]
            [next.jdbc.sql :as sql]))

(defn count*
  [db table]
  (let [ds (jdbc/get-datasource db)]
    (sql/query ds
               (honey/format (q/count* table)))))
