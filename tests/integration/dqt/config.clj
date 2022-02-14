(ns dqt.config)

(def db
  {:dbtype     "postgresql"
   :dbname     "postgres"
   :host       "localhost"
   :port       5432
   :user       "postgres"
   :password   "postgres"
   :ssl        false
   :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(def migratus
  {:store                :database
   :migration-dir        "tests/integration/migrations/"
   ;; :init-script          "01.init.sql"
   :init-in-transaction? false
   :migration-table-name "migrations"
   :db                  db})
