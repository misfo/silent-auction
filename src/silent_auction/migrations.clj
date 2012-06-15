(ns silent-auction.migrations
  (:require [clojure.java.jdbc :as sql]
            [silent-auction.models.db :as db]))

(defn create-items
  []
  (sql/with-connection db/connection
    (sql/create-table :items
      [:id                :serial "PRIMARY KEY"]
      [:complete          :varchar]
      [:category          :varchar]
      [:item              :varchar]
      [:description       :varchar]
      [:donor_name        :varchar]
      [:name_for_listing  :varchar]
      [:address           :varchar]
      [:city              :varchar]
      [:state             :varchar]
      [:zip               :varchar]
      [:solicited_by      :varchar]
      [:fair_market_value :varchar]
      [:created_at        :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])))

(defn -main
  []
  (print "Migrating database...")
  (flush)
  (create-items)
  (println " done."))