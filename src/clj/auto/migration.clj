(ns auto.migration
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [auto.db :as db]))

(defn load-config []
  {:datastore  (jdbc/sql-database db/db-spec)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))