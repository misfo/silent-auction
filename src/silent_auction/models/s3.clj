(ns silent-auction.models.s3
  (:use [environ.core :only [env]])
  (:require [aws.sdk.s3 :as s3]))

(def cred {:access-key (env :aws-access-key)
           :secret-key (env :aws-secret-key)})

(def bucket "silent-auction")

(def put-object   (partial s3/put-object   cred bucket))
(def list-objects (partial s3/list-objects cred bucket))

(defn list-item-objects
  [item-id]
  (list-objects {:prefix (str item-id "/")}))