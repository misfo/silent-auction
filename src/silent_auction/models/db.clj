(ns silent-auction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def connection (System/getenv "DATABASE_URL"))

(defn insert
  [table records]
  (sql/with-connection connection
    (apply sql/insert-records table records)))