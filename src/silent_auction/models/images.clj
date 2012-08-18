(ns silent-auction.models.images
  (:require [nl.zeekat.imajine.core :as imajine]))

(def thumbnail-dimensions [360 286])

(defn s3-key
  ([item-id orig-filename]
     (s3-key item-id orig-filename nil))
  ([item-id orig-filename variation]
     (let [[_ pre-ext ext] (re-find #"(.+?)(\.\w+)?$" orig-filename)]
       (str item-id "/" pre-ext (when variation (str "-" variation)) ext))))

(defn thumbnail
  [input-stream]
  (let [image (imajine/read-image input-stream)
        resized-image (apply imajine/resize-to-bounding-box image thumbnail-dimensions)]
    (imajine/image-stream resized-image "JPG")))