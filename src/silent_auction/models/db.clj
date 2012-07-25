(ns silent-auction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def connection (or (System/getenv "DATABASE_URL")
                    "postgresql://localhost:5432/silent-auction"))

(defn select-items
  []
  (sql/with-connection connection
    (sql/with-query-results items
      [(str "SELECT items.*, categories.name AS category_name "
            "FROM items "
            "LEFT JOIN categories ON items.category_id = categories.id")]
      (vec items))))

(defn select-item
  [id]
  (sql/with-connection connection
    (sql/with-query-results items
      ["SELECT * FROM items WHERE id = ?" (Integer/parseInt id)]
      (first items))))

(def categories
  (memoize (fn []
    (sql/with-connection connection
      (sql/with-query-results cats
        ["SELECT * FROM categories ORDER BY priority DESC, name"]
        (vec cats))))))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))