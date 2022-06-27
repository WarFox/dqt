(ns dqt.calculated-metrics)

(defn get-metric
  "Get given `metric` value for `column-name` in `metrics`"
  [metric metrics column-name]
  (let [k (keyword (format "%s-%s" metric (name column-name)))]
    (k metrics)))

(defn values-count
  "Get count of `column-name` in `metrics`"
  [metrics column-name]
  (get-metric "count" metrics column-name))

(defn variance
  "Get variance of `column-name` in `metrics`"
  [metrics column-name]
  (get-metric "variance" metrics column-name))

(defn -missing-count
  [metrics column-name]
  (- (:row-count metrics)
     (values-count metrics column-name)))

(defn -missing-percentage
  ;; missing count * 100 / row-count
  [metrics column-name]
  (/
    (* 100
       (-missing-count metrics column-name))
    (:row-count metrics)))

(defn -values-percentage
  ;; values-count * 100 / row-count
  [metrics column-name]
  (/
    (* 100
       (values-count metrics column-name))
    (:row-count metrics)))
