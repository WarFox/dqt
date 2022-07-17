(ns user
  (:require
    [dqt.core :as core]
    [honey.sql :as honey]
    [honey.sql.helpers :refer [select from where]]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]))


(def db
  {:dbtype     "postgresql"
   :dbname     "postgres"
   :host       "localhost"
   :port       5432
   :user       "postgres"
   :password   "postgres"
   :ssl        false
   :sslfactory "org.postgresql.ssl.NonValidatingFactory"})


(def sqlmap
  {:select [:first-name :last-name :hired-date]
   :from    [:employees]
   :where   [:= :employee-id 100]})


;; information-schema column
(->> {:select [:column-name :data-type :is-nullable]
      :from   :information_schema.columns
      :where  [:= :table-name "employees"]}
     honey/format
     (jdbc/execute! db))


(-> (select [[:count :*]])
    (from :foo)
    honey/format)


(-> (select :*)
    (select :employee-id)
    (select [[:count :*] :my-count])
    (select [[:avg :salary] :average-salary])
    (from :foo)
    (where [:= :a 1] [:< :b 100])
    honey/format)


(-> {:select [:id
              [[:over
                [[:avg :salary]
                 {:partition-by [:department]
                  :order-by     [:designation]}
                 :Average]
                [[:max :salary]
                 :w
                 :MaxSalary]]]]
     :from   [:employee]
     :window [:w {:partition-by [:department]}]}
    (honey/format {:pretty true}))


(-> (select :%count.*) (from :foo) honey/format)

(-> (select [:%count.*]) (from :foo) honey/format)

(-> (select [[:count :*]]) (from :foo) honey/format)

(-> (select :%max.id) (from :foo) honey/format)


;; not a list, but vectors or symbols
(-> (select [[:count :*] :my-count]
            :employee-id
            [[:avg :salary] :average-salary])
    honey/format)


;; need a list
(-> {:select [[[:count :*] :my-count]
              :employee-id
              [[:avg :salary] :average-salary]]}
    honey/format)


;; function call with :% notation
(-> {:select [[:%count.* :my-count]
              :employee-id
              [:%avg.salary :average-salary]]}
    honey/format)


(jdbc/execute! ds
               (honey/format (count* :employees)))


(sql/query ds
           (honey/format (count* :employees)))


(comment

  (defn create-files
    []
    (migratus/create {:migration-dir "migrations"} "regions")
    (migratus/create {:migration-dir "migrations"} "countries")
    (migratus/create {:migration-dir "migrations"} "locations")
    (migratus/create {:migration-dir "migrations"} "departments")
    (migratus/create {:migration-dir "migrations"} "jobs")
    (migratus/create {:migration-dir "migrations"} "employees")
    (migratus/create {:migration-dir "migrations"} "dependents")))


(defn this-jar
  "utility function to get the name of jar in which this function is invoked"
  [& [ns]]
  (-> (or ns (class *ns*))
      .getProtectionDomain .getCodeSource .getLocation .toURI .getPath))


(defn version-3
  []
  (this-jar "dqt.cli"))


(defn version-2
  []
  (let [location (..
                   (Class/forName "dqt.core.$_main")
                   getProtectionDomain
                   getCodeSource
                   getLocation)]
    (println location)
    (println "Version "
             (.getValue
               (..
                 (java.util.jar.Manifest.
                   (.openStream
                     (java.net.URL.
                       (str "jar:" location "!/META-INF/MANIFEST.MF"))))
                 getMainAttributes)
               "Build-number"))))
