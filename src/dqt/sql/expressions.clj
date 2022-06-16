(ns dqt.sql.expressions
  (:require
   [camel-snake-kebab.core :as csk]))

(defn -as
  "keyword formatted name for \"as\" column"
  [expr column-name]
  (csk/->kebab-case-keyword (format "%s-%s" (name expr) (name column-name))))

(defn -avg
  "HoneySQL expression for average value of a column"
  [column-name]
  [[:avg column-name] (-as :avg column-name)])

(defn -avg-length
  "HoneySQL expression for avg-length of a column"
  [column-name]
  [[:avg [[:length column-name]]] (-as :avg-length column-name)])

(defn -max
  "HoneySQL expression for max value of a column"
  [column-name]
  [[:max column-name] (-as :max column-name)])

(defn -max-length
  "HoneySQL expression for max-length of a column"
  [column-name]
  [[:max [[:length column-name]]] (-as :max-length column-name)])

(defn -min
  "HoneySQL expression for min value of a column"
  [column-name]
  [[:min column-name] (-as :min column-name)])

(defn -min-length
  "HoneySQL expression for min-length of a column"
  [column-name]
  [[:min [[:length column-name]]] (-as :min-length column-name)])

(defn -stddev
  "HoneySQL expression for stddev of a column"
  [column-name]
  [[:stddev column-name] (-as :stddev column-name)])

(defn -sum
  "HoneySQL expression for sum of a column"
  [column-name]
  [[:sum column-name] (-as :sum column-name)])

(defn -variance
  "HoneySQL expression for variance of a column"
  [column-name]
  [[:variance column-name] (-as :variance column-name)])
