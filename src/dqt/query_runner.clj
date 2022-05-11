(ns dqt.query-runner
  (:require [honey.sql :as honey]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn execute!
  "Execute query and builds result set with keys in kebab case"
  ([db query]
   (execute! db query {}))
  ([db query opts]
   (let [formatted-query (honey/format query)]
     (println formatted-query)
     (jdbc/execute! db formatted-query
                    (assoc opts :builder-fn rs/as-kebab-maps)))))
