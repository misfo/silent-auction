(ns silent-auction.models.items
  (:require [valip.core :as valip]))

(defn- parse-usd
  [text]
  (when (seq text)
    (let [usd (BigDecimal. text)]
      (if (> (.scale usd) 2)
        (throw (NumberFormatException.)
        usd)))))

(defn- valid-usd?
  [text]
  (try
    (parse-usd text)
    true
    (catch NumberFormatException _ false)))

(defn validate
  [it]
  (valip/validate it
    [:estimated_market_value valid-usd? "Must be a valid US dollar amount"]))

(defn parse
  [it]
  (letfn [(parse-int [text] (when (seq text) (Integer/parseInt text)))]
    (-> it
      (update-in [:estimated_market_value] parse-usd)
      (update-in [:category_id] parse-int))))
