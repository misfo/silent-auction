(ns silent-auction.import
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [silent-auction.models.db :as db]))

(def expected-column-headers
  ["Item Complete?"
   "Category"
   "Item"
   "Description"
   "Donor Name"
   "Name for Listing"
   "Address"
   "City, State, Zip"
   "Solicited by"
   "Fair Mkt. Value"
   "Starting Bid"
   "Bid Increment"
   "Winning Bid"
   "Purchaser"])

(defn- split-city-state-zip
  [text]
  (let [[_ city state zip] (re-matches #"(?i:(.+)(?:,\s*)([A-Z]{2})(?:\s*(\d+)?)?)" text)]
    {:city city
     :state (when state (string/upper-case state))
     :zip zip}))

(defn- item-row->map
  [[complete
    category
    item
    description
    donor_name
    name_for_listing
    address
    city-state-zip
    solicited_by
    fair_market_value]]
  (merge (split-city-state-zip city-state-zip)
         {:complete complete
          :category category
          :item item
          :description description
          :donor_name donor_name
          :name_for_listing name_for_listing
          :address address
          :solicited_by solicited_by
          :fair_market_value fair_market_value}))

(defn- import-rows
  [rows]
  (let [items (map item-row->map rows)]
    (db/insert :items items)))

(defn -main
  [filename]
  (with-open [csv-reader (io/reader filename)]
    (let [[header & rows] (csv/read-csv csv-reader)]
      (assert (= header expected-column-headers) (str "Unexpected column header: " header))
      (import-rows rows)
      ; (let [show-split (fn [[_ _ _ _ _ _ _ csz]]
      ;                    (str csz ": " (split-city-state-zip csz)))
      ;       split-strings (map show-split rows)]
      ;   (doall (map println split-strings)))
      (println "Imported " (count rows) " items"))))