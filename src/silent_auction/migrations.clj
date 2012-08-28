(ns silent-auction.migrations
  (:require [clojure.java.jdbc :as sql]
            [ragtime.core :as ragtime]
            [ragtime.strategy :as strategy]
            ragtime.sql.postgres
            [silent-auction.models.db :as db]))

(def initial-ddls
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

(def migrations
  [{:id "initial-schema"
    :up (fn [connection]
          (sql/with-connection connection
            (apply sql/do-commands initial-ddls)
            (apply sql/insert-records :categories categories)))
    :down (fn [_] (throw (RuntimeException.)))}
   {:id "add-price"
    :up (fn [connection]
          (sql/with-connection connection
            (sql/do-commands "ALTER TABLE items ADD COLUMN price varchar")))
    :down (fn [connection]
            (sql/with-connection connection
              (sql/do-commands "ALTER TABLE items DROP COLUMN price")))}
   {:id "add-image-columns"
    :up (fn [connection]
          (sql/with-connection connection
            (sql/do-commands "ALTER TABLE items ADD COLUMN thumbnail_url varchar, ADD COLUMN photo_by varchar")))
    :down (fn [connection]
            (sql/with-connection connection
              (sql/do-commands "ALTER TABLE items DROP COLUMN thumbnail_url, DROP COLUMN photo_by")))}
   {:id "create-users"
    :up (fn [connection]
          (sql/with-connection connection
            (sql/create-table :users
                              [:email            :varchar "NOT NULL"]
                              [:crypted_password :varchar "NOT NULL"]
                              [:created_at       :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])))
    :down (fn [connection]
            (sql/with-connection connection
              (sql/drop-table :users)))}])


(defn -main
  []
  (doseq [migration migrations]
    (ragtime/remember-migration migration))
  (let [applied  (ragtime/applied-migrations db/connection)]
    (println "applied:\n" applied)
    (println "strategy:" (strategy/raise-error applied migrations)))
  (println "\nMigrating database...")
  (ragtime/migrate-all db/connection migrations)
  (println "Done."))

