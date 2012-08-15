(ns silent-auction.models.test.items
  (:use clojure.test)
  (:require [silent-auction.models.items :as items]))

(deftest validate-test
  (is (not (= (:estimated_market_value (items/validate {:estimated_market_value "123456789"}))
              nil)))
  (is (= (:estimated_market_value (items/validate {:estimated_market_value "12345678"}))
         nil)))

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