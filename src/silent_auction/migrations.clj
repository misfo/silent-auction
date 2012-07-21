(ns silent-auction.migrations
  (:require [clojure.java.jdbc :as sql]
            [silent-auction.models.db :as db]))

(def table-ddls
  [(sql/create-table-ddl :categories
     [:id         :serial "PRIMARY KEY"]
     [:name       :varchar]
     [:priority   :integer "DEFAULT 0"]
     [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])
   (sql/create-table-ddl :items
     [:id                     :serial "PRIMARY KEY"]
     [:category_id            :integer "REFERENCES categories(id)"]
     [:item_id                :varchar]
     [:title                  :varchar]
     [:description            :varchar]
     [:donor                  :varchar]
     [:estimated_market_value "decimal(10, 2)"]
     [:fineprint              :varchar]
     [:created_at             :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])])

(def categories
  [{:name "Live Auction", :priority 1}
   {:name "Art & Decor"}
   {:name "Children"}
   {:name "Dining"}
   {:name "Entertainment"}
   {:name "Fashion"}
   {:name "Getaways"}
   {:name "Lifestyle"}
   {:name "One of a Kind"}
   {:name "Sports"}
   {:name "Wine & Spirits"}])

(defn -main
  []
  (println "Using the the schema:")
  (apply println table-ddls)
  (println "\nMigrating database...")
  (sql/with-connection db/connection
    (apply sql/do-commands table-ddls)
    (apply sql/insert-records :categories categories))
  (println "Done."))