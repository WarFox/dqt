(ns dqt.metrics
  (:require
   [dqt.query-runner :as q]
   [dqt.sql.expressions :as expressions]))

(def metrics-fns
  "Map of metrics and functions"
  {:avg                expressions/-avg
   :avg-length         expressions/-avg-length
   :duplicate-count    identity
   :frequent-values    identity
   :histogram          identity
   :invalid-count      identity
   :invalid-percentage identity
   :max                expressions/-max
   :max-length         expressions/-max-length
   :maxs               identity
   :min                expressions/-min
   :min-length         expressions/-min-length
   :mins               identity
   :missing-count      identity
   :missing-percentage identity
   :row-count          [[:count :*] :row-count]
   :stddev             expressions/-stddev
   :sum                expressions/-sum
   :unique-count       identity
   :uniqueness         identity
   :valid-count        identity
   :valid-percentage   identity
   :values-count       identity
   :values-percentage  identity
   :variance           expressions/-variance})

(def metrics-for-data-type
  {:integer           [:avg :max :min :stddev :sum :variance]
   :numeric           [:avg :max :min :stddev :sum :variance]
   :character-varying [:avg-length :min-length :max-length]
   :string            [:avg-length :min-length :max-length]})

(defn- fields
  [selected-metrics {:keys [columns/metrics columns/column-name]}]
  (mapv #((selected-metrics %) column-name) metrics))

(defn get-select-map
  "Returns honeysql map of select query for given column metrics"
  [columns metrics]
  (let [selected-metrics (select-keys metrics-fns metrics)
        expressions      (apply concat (mapv #(fields selected-metrics %) columns))]
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
