(ns silent-auction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def connection (or (System/getenv "DATABASE_URL")
                    "postgresql://localhost:5432/silent-auction"))

(defn select-items
  []
  (sql/with-connection connection
    (sql/with-query-results items
      ["SELECT * FROM items"]
      (vec items))))

(defn select-item
  [id]
  (sql/with-connection connection
    (sql/with-query-results items
      ["SELECT * FROM items WHERE id = ?" (Integer/parseInt id)]
      (first items))))

(def categories
  (sql/with-connection connection
    (sql/with-query-results cats
      ["SELECT DISTINCT category FROM items"]
      (vec (map :category cats)))))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))