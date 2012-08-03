(ns silent-auction.models.test.items
  (:use clojure.test)
  (:require [silent-auction.models.items :as items]))

(deftest parse-test
  (is (= (items/parse {:category_id "1"
                       :estimated_market_value "123.4"})
         {:category_id 1
          :estimated_market_value 123.4M}))
  (is (= (items/parse {:category_id "1"
                       :estimated_market_value "123.4"
                       :id "123"
                       :some_whacky_param "sdlfkj"})
         {:category_id 1
          :estimated_market_value 123.4M})))