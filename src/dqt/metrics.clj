(ns dqt.metrics
  (:require [clojure.string :as str]
            [honey.sql :as honey]))

(defn -as
  [expr column-name]
  (keyword (format "%s-%s" (name expr) (name column-name))))

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

(def data-type-metrics
  {:integer           [:avg :min :max]
   :numeric           [:avg :min :max]
   :character-varying [:avg-length]
   :string            [:avg-length]})

(defn- apply-expr
  [selected-metrics metric]
  (let [expr (selected-metrics metric)]
    (expr :column-name)))

(defn get-select-map
  "Returns honeysql map for selecting columns "
  [metrics]
  (let [selected-metrics (select-keys supported-metrics metrics)]
    ;; TODO select-distinct if distinct
    {:select (mapv #(apply-expr selected-metrics %) metrics)}))

(defn enrich-column-metadata
  "Enrich with metrics for given column based on data_type"
  [column]
  (let [data-type (-> (:columns/data_type column) (str/replace " " "-") keyword)]
    (assoc column :columns/metrics (data-type data-type-metrics))))

(defn format-sql
  [metrics table-name]
  (-> (get-select-map metrics)
      (merge {:from table-name})
      honey/format))
