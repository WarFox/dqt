(ns dqt.core-test
  (:require [clojure.test :refer :all]
            [dqt.core :as sut]
            [dqt.system :as system]))

(deftest app-test
  (testing "app initialises system"
    (with-redefs [system/init (fn [_] :system)]
      (is (= :system
             (sut/app {:config :config}))))))
