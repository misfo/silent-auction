(ns silent-auction.handler
  (:use compojure.core
        [ring.middleware.json :only [wrap-json-response]]
        hiccup.bootstrap.middleware)
  (:require [clojure.string :as str]
            [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.urls :as urls]
            [silent-auction.views.core :as views]
            [silent-auction.models.db :as db]
            [silent-auction.models.images :as images]
            [silent-auction.models.items :as items]
            [silent-auction.models.s3 :as s3]))

(defroutes admin-routes
  (GET "/item/new" []
    (views/create-item-modal))
  (POST "/item/" {params :params}
    (if-let [errors (items/validate params)]
      (-> (response/response errors)
        (assoc :status 400))
      (do
        (db/insert :items [(items/parse params)])
        (response/response {}))))
  (GET "/item/:id" [id]
    (views/edit-item-modal (db/select-item id)))
  (POST "/item/:id" {params :params}
    (if-let [errors (items/validate params)]
      (-> (response/response errors)
        (assoc :status 400))
      (do
        ; assume nil if checkbox isn't checked
        (let [item-params (update-in params [:price] identity)]
          (db/update-item (:id params) (items/parse item-params)))
        (response/response {}))))
  (GET "/item/:id/upload" [id]
    (views/upload-modal id))
  (POST "/item/:id/upload" [id photo_by image-data]
    (let [{:keys [content-type filename size tempfile]} image-data
          thumb-key (images/s3-key id filename "thumbnail")]
      (s3/put-public-object (images/s3-key id filename) tempfile)
      (s3/put-public-object thumb-key (images/thumbnail tempfile))
      (db/update-item id {:photo_by (when-not (str/blank? photo_by) photo_by)
                          :thumbnail_url (s3/url thumb-key)}))
    (response/redirect-after-post "/"))
  (POST "/item/:id/delete" [id]
    (db/delete-rows :items ["id = ?" (Integer/parseInt id)])
    ""))

(def admin-app
  (-> admin-routes
    wrap-json-response))

(defroutes app-routes
  (GET "/" [] (views/items (db/select-items)))
  (context urls/admin-root [] admin-app)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
