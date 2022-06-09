(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]))

(def lib 'io.github.warfox/dqt)
(def version "0.1.0-SNAPSHOT")
(def main 'dqt.core)

(defn clean
  "Delete target directory"
  [opts]
  (-> opts
      (bb/clean)))

(defn test
  "Run the tests."
  [opts]
  (bb/run-tests opts))

(def manifest-entries
  {"Build-Jdk-Spec"         (System/getProperty "java.specification.version")
   "Implementation-Version" version})

(defn uberjar
  "Build the uberjar."
  [opts]
  (-> opts
      (assoc :lib lib
             :version version
             :main main
             :manifest manifest-entries)
      (bb/clean)
      (bb/uber)))
