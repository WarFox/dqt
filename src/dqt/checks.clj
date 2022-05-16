(ns dqt.checks)

(defn run-check
  "Return true if predicate returns false"
  [check metrics]
  (let [[metric-column op value] check
        f (resolve op)
        result                   (apply f [(metric-column metrics) value])]
    (printf "Test: [%s (%s %s %s)] is %s\n" metric-column op (metric-column metrics) value result)
    [check result]))
