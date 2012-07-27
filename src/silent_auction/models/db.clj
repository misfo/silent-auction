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

(defn delete-item
  [table where-params]
  (sql/with-connection connection
    (sql/delete-rows table where-params)))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))

(defn insert-item
  [it]
  (let [parse-usd (fn [text] (when (seq text) (Double/parseDouble text)))
        parse-int (fn [text] (when (seq text) (Integer/parseInt text)))
        parsed-it (-> it
                    (update-in [:estimated_market_value] parse-usd)
                    (update-in [:category_id] parse-int))]
    (insert :items [parsed-it])))
