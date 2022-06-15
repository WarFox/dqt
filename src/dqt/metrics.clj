(ns dqt.metrics
  (:require
   [camel-snake-kebab.core :as csk]
   [dqt.query-runner :as q]))

(defn -as
  [expr column-name]
  (csk/->kebab-case-keyword (format "%s-%s" (name expr) (name column-name))))

(defn -avg [column-name]
  [[:avg column-name] (-as :avg column-name)])

(defn -avg-length
  [column-name]
  [[:avg [[:length column-name]]] (-as :avg-length column-name)])

(defn -max
  [column-name]
  [[:max column-name] (-as :max column-name)])

(defn -max-length
  [column-name]
  [[:max [[:length column-name]]] (-as :max-length column-name)])

(defn -min
  [column-name]
  [[:min column-name] (-as :min column-name)])

(def metrics-fns
  "Map of metrics and functions"
  {:row-count          [[:count :*] :row-count]
   :avg                -avg
   :avg-length         -avg-length
   :duplicate-count    identity
   :frequent-values    identity
   :histogram          identity
   :invalid-count      identity
   :invalid-percentage identity
   :max                -max
   :maxs               identity
   :max-length         -max-length
   :min                -min
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

(def metrics-for-data-type
  {:integer           [:avg :min :max]
   :numeric           [:avg :min :max]
   :character-varying [:avg-length]
   :string            [:avg-length]})

(defn- apply-expr
  [selected-metrics {:keys [columns/metrics columns/column-name]}]
  (mapv #((selected-metrics %) column-name) metrics))

(defn get-select-map
  "Returns honeysql map of select query for given column metrics"
  [columns metrics]
  (let [selected-metrics (select-keys metrics-fns metrics)
        expressions      (apply concat (mapv #(apply-expr selected-metrics %) columns))]
;; TODO select-distinct if distinct
;; TODO check if row-count is needed
    (if (some #{:row-count} metrics)
      {:select (conj expressions (:row-count metrics-fns))}
      {:select expressions})))

(defn enrich-column-metadata
  "Enrich metadata with metrics for given column based on data-type"
  [{:keys [columns/data-type] :as column}]
  (assoc column
         :columns/metrics (data-type metrics-for-data-type)))

(defn get-metrics
  [db table-name columns metrics]
  (when (empty? columns)
    (throw (ex-info "columns-metadata is empty. Is the table name correct?"
                    {:table-name table-name})))
  (let [enriched-columns (mapv enrich-column-metadata columns)
        query            (-> (get-select-map enriched-columns metrics)
                             (assoc :from table-name))]
    (q/execute-one! db query)))
