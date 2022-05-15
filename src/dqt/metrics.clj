(ns dqt.metrics
  (:require [clojure.string :as str]
            [honey.sql :as honey]
            [dqt.query-runner :as q]
            [dqt.information-schema :as info-schema]))

(defn -as
  [expr column-name]
  (keyword (format "%s-%s" (name expr) (name column-name))))

(def supported-metrics
  {:row-count          [[:count :*] :row-count]
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
  [selected-metrics {:keys [columns/metrics columns/column-name]}]
  (mapv #((selected-metrics %) column-name) metrics))

(defn get-select-map
  "Returns honeysql map for selecting columns"
  [columns metrics]
  (let [selected-metrics (select-keys supported-metrics metrics)
        expressions      (apply concat (mapv #(apply-expr selected-metrics %) columns))]
;; TODO select-distinct if distinct
;; TODO check if row-count is needed
    (if (some #{:row-count} metrics)
      {:select (conj expressions (:row-count supported-metrics))}
      {:select expressions})))

(defn enrich-column-metadata
  "Enrich with metrics for given column based on data_type"
  [{:keys [columns/data-type] :as column}]
  (assoc column
         :columns/metrics (data-type data-type-metrics)))

(defn get-metrics
  [db table-name metrics]
  (let [columns          (info-schema/get-columns-metadata db table-name)
        enriched-columns (mapv enrich-column-metadata columns)
        query            (-> (get-select-map enriched-columns metrics)
                             (assoc :from table-name))]
    (q/execute! db query)))
