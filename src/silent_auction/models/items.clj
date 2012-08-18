(ns silent-auction.models.items
  (:require [clojure.string :as str]
            [valip.core :as valip])
  (:use [valip.predicates :only [present?]]))

(def user-input-attrs [:category_id
                       :item_id
                       :title
                       :description
                       :donor
                       :estimated_market_value
                       :price
                       :fineprint])

(def price-types #{"priceless"})

(defn- parse-usd
  [text]
  (when (seq text)
    (let [usd (BigDecimal. text)
          scale (.scale usd)
          integer-digits (- (.precision usd) scale)]
      (if (or (> scale 2) (> integer-digits 8))
        (throw (NumberFormatException.))
        usd))))

(defn- valid-usd?
  [text]
  (try
    (parse-usd text)
    true
    (catch NumberFormatException _ false)))

(defn validate
  [it]
  (valip/validate it
    [:title present? "Must be supplied"]
    [:donor present? "Must be supplied"]
    [:price
     (if (str/blank? (:estimated_market_value it))
       price-types
       (constantly true))
     "A price must be specified"]
    [:estimated_market_value valid-usd? "Must be a valid US dollar amount"]
    [:estimated_market_value
     #(or (str/blank? (:price it)) (str/blank? %))
     "Cannot be specified for priceless items"]))

(defn parse
  [it]
  (letfn [(parse-int [text] (when (seq text) (Integer/parseInt text)))]
    (-> it
      (select-keys user-input-attrs)
      (update-in [:estimated_market_value] parse-usd)
      (update-in [:category_id] parse-int))))
