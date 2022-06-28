(ns dqt.checks)

(defn run-check
  "Return true if predicate returns false"
  [check metrics]
  (let [[metric-column op value] check
        f                        (if (= op =) #'clojure.core/== (resolve op))]
    (if-some [metric-value (get metrics metric-column)]
      (let [result (apply f [metric-value value])]
        (printf "Test: [%s (%s %s %s)] is %s\n" metric-column op metric-value value result)
        [check result])
      (do
        (println metric-column "is nil")
        [check nil]))))
