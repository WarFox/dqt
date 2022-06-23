(ns dqt.metrics
  (:require
   [camel-snake-kebab.core :as csk]
   [dqt.query-runner :as q]
   [dqt.sql.expressions :as expressions]))

(defn get-metric
  "Get given `metric` value for `column-name` in `metrics`"
  [metric metrics column-name]
  (let [k  (keyword (format "%s-%s" metric (name column-name)))]
    (k metrics)))

(defn get-count
  "Get count of `column-name` in `metrics`"
  [metrics column-name]
  (get-metric "count" metrics column-name))

(defn get-variance
  "Get variance of `column-name` in `metrics`"
  [metrics column-name]
  (get-metric "variance" metrics column-name))

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

(def sql-metrics-group
  "sql metrics grouped by data-type"
  {:integer           [:avg :max :min :stddev :sum :variance]
   :numeric           [:avg :max :min :stddev :sum :variance]
   :any               [:values-count]
   :date              [:max :min]
   :character-varying [:avg-length :min-length :max-length]
   :string            [:avg-length :min-length :max-length]})

(def calculated-metrics-group
  "Calculated metrics grouped by data-type"
  {:any [:missing-count]})

(defn- field-expression
  [metric-fns metric column-name]
  (let [f (get metric-fns metric)]
    (f column-name)))

(defn fields
  "Returns a list of honeysql fields for the given column based on `metrics-fns`"
  [metric-fns {:keys [columns/sql-metrics columns/column-name]}]
  (map #(field-expression metric-fns % column-name) sql-metrics))

(defn sql-metrics-map
  "Returns a honeysql map of select query for given `columns` and `table-name`"
  ;; TODO pass metrics-fn as argument for proper testing
  [columns table-name]
  (let [fields-list (apply concat (map #(fields metrics-fns %) columns))]
    ;; TODO select-distinct if distinct
    {:select (conj fields-list (:row-count metrics-fns))
     :from   table-name}))

(defn required-metrics
  [metrics-group data-type metrics]
  (apply conj
         (filter #(some #{%} metrics) (get metrics-group data-type))
         (:any metrics-group)))

(defn enrich-column-metadata
  "Add list of sql-metrics and calculated metrics for `column` based on the data-type"
  [{:keys [columns/data-type] :as column} metrics]
  (assoc column
         :columns/sql-metrics (required-metrics sql-metrics-group data-type metrics)
         :columns/calculated-metrics (required-metrics calculated-metrics-group data-type metrics)))

(defn get-metrics
  [db table-name columns-metadata]
  (when (empty? columns-metadata)
    (throw (ex-info "columns-metadata is empty. Is the table name correct?"
                    {:table-name table-name})))
  (q/execute-one! db
                  (sql-metrics-map columns-metadata table-name)))

(defn calculated-metrics
  [{:keys [:columns/calculated-metrics :columns/column-name]} sql-metrics]
  (when (not-empty sql-metrics)
    (merge sql-metrics
           (into {}
                 (map
                  (fn new-metric
                    [metric-name]
                    (let [metric-fn   (metrics-fns metric-name)
                          metric-name (format "%s-%s" (name metric-name) (name column-name))]
                      (hash-map
                       (csk/->kebab-case-keyword metric-name)
                       (metric-fn sql-metrics column-name))))
                  calculated-metrics)))))
