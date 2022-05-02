(ns dqt.metrics
  (:require [honey.sql :as honey]))

(def supported-metrics
  {:row-count          (fn [column-name] [:%count.*])
   :avg                (fn [column-name] [[:avg column-name]])
   :avg-length         (fn [column-name] [[:avg [[:length column-name]]]])
   :duplicate-count    identity
   :frequent-values    identity
   :histogram          identity
   :invalid-count      identity
   :invalid-percentage identity
   :max                identity
   :maxs               identity
   :max-length         identity
   :min                identity
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
  (let [fn (selected-metrics metric)]
    (fn :column-name)))

(defn build-select-map
  [metrics]
  (let [selected-metrics (select-keys supported-metrics metrics)]
    ;; TODO select-distinct if distinct
    {:select (mapv #(build-expr selected-metrics %) metrics)}))

(defn format-sql
  [metrics]
  (-> metrics
      build-select-map
      honey/format))
