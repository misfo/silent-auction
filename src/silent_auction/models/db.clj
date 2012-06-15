(ns silent-auction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def connection (or (System/getenv "DATABASE_URL")
                    "postgresql://localhost:5432/silent-auction"))

(defn select-items
  [func]
  (sql/with-connection connection
    (sql/with-query-results* ["SELECT * FROM items"] func)))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))