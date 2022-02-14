(ns dqt.queries)

(defn count*
  [table]
  {:select [:%count.*]
   :from   [table]})
