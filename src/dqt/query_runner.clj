(ns dqt.query-runner
  (:require [next.jdbc :as jdbc]))

(defn execute!
  ([db query]
   (execute! db query {}))
  ([db query opts]
   (jdbc/execute! db query opts)))
