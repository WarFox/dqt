(ns dqt.metrics
  (:require
   [camel-snake-kebab.core :as csk]
   [dqt.calculated-metrics :as cm]
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
   :missing-count      cm/-missing-count
   :missing-percentage cm/-missing-percentage
   :row-count          [[:count :*] :row-count]
   :stddev             expressions/-stddev
   :sum                expressions/-sum
   :unique-count       identity
   :uniqueness         identity
   :valid-count        identity
   :valid-percentage   identity
   :values-count       expressions/-values-count
   :values-percentage  cm/-values-percentage
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
  {:any [:values-percentage
         :missing-count
         :missing-percentage]})

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
  "Calculated metrics from sql-metrics"
  [{:keys [:columns/calculated-metrics :columns/column-name]} sql-metrics]
  (when (not-empty sql-metrics)
    (into {}
          (map
           (fn new-metric
             [metric-name]
             (let [metric-fn   (metrics-fns metric-name)
                   metric-name (format "%s-%s" (name metric-name) (name column-name))]
               (hash-map
                (csk/->kebab-case-keyword metric-name)
                (metric-fn sql-metrics column-name))))
           calculated-metrics))))
