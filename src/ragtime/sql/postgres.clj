(ns ragtime.sql.postgres
  (:use [ragtime.core :only (Migratable)])
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :as io])
  (:import (java.util Date)))

(def ^:private migrations-table "ragtime_migrations")

(defn ^:internal ensure-migrations-table-exists [db]
  ;; TODO: is there a portable way to detect table existence?
  (sql/with-connection db
    (try
      (sql/create-table migrations-table
                        [:id "varchar(255)"]
                        [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])
      (catch Exception _))))

(defrecord SqlDatabase [classname subprotocol subname user password])

(def ^:private db-methods
  {:add-migration-id (fn [db id]
                       (sql/with-connection db
                         (ensure-migrations-table-exists db)
                         (sql/insert-values migrations-table
                                            [:id] [(str id)])))
  :remove-migration-id (fn [db id]
                         (sql/with-connection db
                           (ensure-migrations-table-exists db)
                           (sql/delete-rows migrations-table ["id = ?" id])))
  :applied-migration-ids (fn [db]
                           (sql/with-connection db
                             (ensure-migrations-table-exists db)
                             (sql/with-query-results results
                               ["SELECT id FROM ragtime_migrations ORDER BY created_at"]
                               (vec (map :id results)))))})

(extend SqlDatabase Migratable db-methods)

(extend String Migratable db-methods)

