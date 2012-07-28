(ns silent-auction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def connection (or (System/getenv "HEROKU_POSTGRESQL_YELLOW_URL")
                    "postgresql://localhost:5432/silent-auction"))

(defn select-items
  []
  (sql/with-connection connection
    (sql/with-query-results items
      [(str "SELECT items.*, categories.name AS category_name "
            "FROM items "
            "LEFT JOIN categories ON items.category_id = categories.id "
            "ORDER BY categories.priority DESC, categories.name, items.title")]
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

(defn used-categories
  []
  (sql/with-connection connection
    (sql/with-query-results cats
      ["SELECT * FROM categories WHERE id IN (SELECT category_id FROM items) ORDER BY priority DESC, name"]
      (vec cats))))

(defn delete-rows
  [table where-params]
  (sql/with-connection connection
    (sql/delete-rows table where-params)))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))

