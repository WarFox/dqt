(ns dqt.checks)

(defn run-check
  "Return true if predicate returns false"
  [check metrics]
  (let [[metric-column op value] check
        result                   (op (metric-column metrics) value)]
    (println (format "Test: (%s %s %s) is %s" metric-column op value result))
    result))
