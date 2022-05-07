(ns dqt.metrics
  (:require [honey.sql :as honey]))

(defn -as
  [expr column-name]
  (keyword (str (name column-name) "-" (name expr))))

(defn- -row-count
  [column-name]
  (if (some #(= column-name %) ["*" :*])
    [[:count :*] :row-count]
    [[:count column-name] (-as :count column-name)]))

(def supported-metrics
  {:row-count          -row-count
   :avg                (fn avg [column-name]
                         [[:avg column-name] (-as :avg column-name)])
   :avg-length         (fn avg-length [column-name]
                         [[:avg [[:length column-name]]] (-as :avg-length column-name)])
   :duplicate-count    identity
   :frequent-values    identity
   :histogram          identity
   :invalid-count      identity
   :invalid-percentage identity
   :max                (fn max
                         [column-name] [[:max column-name] (-as :max column-name)])
   :maxs               identity
   :max-length         identity
   :min                (fn min
                         [column-name] [[:min column-name] (-as :min column-name)])
   :mins               identity
   :min-length         identity
   :missing-count      identity
   :missing-percentage identity
   :stddev             identity
   :sum                identity
   :uniqueness         identity
   :unique-count       identity
   :valid-count        identity
   :valid-percentage   identity
   :values-count       identity
   :values-percentage  identity
   :variance           identity})

(defn- build-expr
  [selected-metrics metric]
  (let [expr (selected-metrics metric)]
    (expr :column-name)))

(defn build-select-map
  [metrics]
  (let [selected-metrics (select-keys supported-metrics metrics)]
    ;; TODO select-distinct if distinct
    {:select (mapv #(build-expr selected-metrics %) metrics)}))

(defn format-sql
  [metrics table-name]
  (-> (build-select-map metrics)
      (merge {:from table-name})
      honey/format))
