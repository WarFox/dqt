(ns dqt.metrics
  (:require
   [dqt.query-runner :as q]
   [dqt.sql.expressions :as expressions]
   [camel-snake-kebab.core :as csk]))

(defn get-count
  "Get count of `column-name` in `metrics`"
  [metrics column-name]
  (->> (name column-name) (format "count-%s") keyword metrics))

(defn get-variance
  [metrics column-name]
  (->> (name column-name) (format "variance-%s") keyword metrics))

(defn -missing-count
  [metrics column-name]
  (- (:row-count metrics)
     (get-count metrics column-name)))

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
   :missing-count      -missing-count
   :missing-percentage identity
   :row-count          [[:count :*] :row-count]
   :stddev             expressions/-stddev
   :sum                expressions/-sum
   :unique-count       identity
   :uniqueness         identity
   :valid-count        identity
   :valid-percentage   identity
   :values-count       expressions/-values-count
   :values-percentage  identity
   :variance           expressions/-variance})

(def metrics-for-data-type
  {:integer           [:avg :max :min :stddev :sum :variance]
   :numeric           [:avg :max :min :stddev :sum :variance]
   :any               [:values-count]
   :date              [:max :min]
   :character-varying [:avg-length :min-length :max-length]
   :string            [:avg-length :min-length :max-length]})

(def calculated-metrics-group
  {:any [:missing-count]})

(defn- fields
  [selected-metric-fns {:keys [columns/metrics columns/column-name]}]
  (map #((selected-metric-fns %) column-name) metrics))

(defn get-select-map
  "Returns honeysql map of select query for given column metrics"
  [columns metrics]
  (let [selected-metric-fns (select-keys metrics-fns metrics)
        fields-list         (apply concat (map #(fields selected-metric-fns %) columns))]
;; TODO select-distinct if distinct
;; TODO check if row-count is needed
    (if (some #{:row-count} metrics)
      {:select (conj fields-list (:row-count metrics-fns))}
      {:select fields-list})))

(defn enrich-column-metadata
  "Enrich metadata with metrics for given column based on data-type"
  [{:keys [columns/data-type] :as column}]
  (assoc column
         :columns/metrics (apply conj
                                 (get metrics-for-data-type data-type)
                                 (:any metrics-for-data-type))
         :columns/calculated-metrics (apply conj
                                            (get calculated-metrics-group data-type)
                                            (:any calculated-metrics-group))))

(defn get-metrics
  [db table-name columns-metadata metrics]
  (when (empty? columns-metadata)
    (throw (ex-info "columns-metadata is empty. Is the table name correct?"
                    {:table-name table-name})))
  (let [query (-> (get-select-map columns-metadata metrics)
                  (assoc :from table-name))]
    (q/execute-one! db query)))

(defn calculated-metrics
  [{:keys [:columns/calculated-metrics :columns/column-name]} sql-metrics]
  (let [selected-metric-fns (select-keys metrics-fns calculated-metrics)]
    (merge sql-metrics
           (into {}
                 (map
                  (fn new-metric [metric-name]
                    (let [metric-fn   (selected-metric-fns metric-name)
                          metric-name (format "%s-%s" (name metric-name) (name column-name))]
                      (hash-map
                       (csk/->kebab-case-keyword metric-name)
                       (metric-fn sql-metrics column-name))))
                  calculated-metrics)))))
