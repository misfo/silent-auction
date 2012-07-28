(ns silent-auction.models.items)

(defn parse
  [it]
  (letfn [(parse-usd [text] (when (seq text) (Double/parseDouble text)))
          (parse-int [text] (when (seq text) (Integer/parseInt text)))]
    (-> it
      (update-in [:estimated_market_value] parse-usd)
      (update-in [:category_id] parse-int))))
