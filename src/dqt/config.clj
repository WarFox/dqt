(ns dqt.config)

(def pg-db {:dbtype     "postgresql"
            :dbname     "postgres"
            :host       "localhost"
            :user       "postgres"
            :password   "postgres"
            :ssl        false
            :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(def config {:store                :database
             :migration-dir        "migrations/"
             :init-script          "01.init.sql" ; script should be located in the :migration-dir path
             :init-in-transaction? false
             ; defaults to true, some databases do not support
             ; schema initialization in a transaction
             :migration-table-name "foo_bar"
             :db                   pg-db})
