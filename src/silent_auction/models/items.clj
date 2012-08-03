(ns silent-auction.models.items
  (:require [valip.core :as valip])
  (:use [valip.predicates :only [present?]]))

(def user-input-attrs [:category_id
                       :item_id
                       :title
                       :description
                       :donor
                       :estimated_market_value
                       :fineprint])

(defn- parse-usd
  [text]
  (when (seq text)
    (let [usd (BigDecimal. text)]
      (if (> (.scale usd) 2)
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
    [:estimated_market_value valid-usd? "Must be a valid US dollar amount"]))

(defn parse
  [it]
  (letfn [(parse-int [text] (when (seq text) (Integer/parseInt text)))]
    (-> it
      (select-keys user-input-attrs)
      (update-in [:estimated_market_value] parse-usd)
      (update-in [:category_id] parse-int))))
