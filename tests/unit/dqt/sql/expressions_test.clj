(ns dqt.sql.expressions-test
  (:require
   [dqt.sql.expressions :as sut]
   [clojure.test :refer :all]))


(deftest -avg-length-test
  (is (= [[:avg [[:length :my-column]]] :avg-length-my-column]
         (sut/-avg-length :my-column))))

(deftest -max-test
  (is (= [[:max :my-column] :max-my-column]
         (sut/-max :my-column))))

(deftest -max-length-test
  (is (= [[:max [[:length :my-column]]] :max-length-my-column]
         (sut/-max-length :my-column))))

(deftest -min-test
  (is (= [[:min :my-column] :min-my-column]
         (sut/-min :my-column))))

(deftest -min-length-test
  (is (= [[:min [[:length :my-column]]] :min-length-my-column]
         (sut/-min-length :my-column))))

(deftest -stddev-test
  (is (= [[:stddev :my-column] :stddev-my-column]
         (sut/-stddev :my-column))))

(deftest -sum-test
  (is (= [[:sum :my-column] :sum-my-column]
         (sut/-sum :my-column))))

(deftest -variance-test
  (is (= [[:variance :my-column] :variance-my-column]
         (sut/-variance :my-column))))
