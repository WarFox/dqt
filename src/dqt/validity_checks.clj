(ns dqt.validity-checks
  (:require [dqt.query-runner :as q]))

;; select EmailAddress from FindInvalidEmailAddressDemo
;; -> where EmailAddress NOT LIKE '%_@_%._%';

(def email-regex #"%_@_%._%")

(defn email
  "Returns list of invalid emails from the column"
  [column table]
  {:select [column]
   :from table
   :where [column :not :like email-regex]})
