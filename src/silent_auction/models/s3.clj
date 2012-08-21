(ns silent-auction.models.s3
  (:use [environ.core :only [env]]
        [ring.util.codec :only [url-encode]])
  (:require [clojure.string :as str]
            [aws.sdk.s3 :as s3]))

(def cred {:access-key (env :aws-access-key)
           :secret-key (env :aws-secret-key)})

(def bucket "silent-auction")

(def put-object        (partial s3/put-object        cred bucket))
(def list-objects      (partial s3/list-objects      cred bucket))
(def update-object-acl (partial s3/update-object-acl cred bucket))

(defn put-public-object
  [key value]
  (put-object key value)
  (update-object-acl key
    (s3/grant :all-users :read)))

(defn list-item-objects
  [item-id]
  (list-objects {:prefix (str item-id "/")}))

(defn url
  [key]
  (let [encoded-key (str/join "/" (map url-encode (str/split key #"/")))]
    (str "http://s3.amazonaws.com/" bucket "/" encoded-key)))

