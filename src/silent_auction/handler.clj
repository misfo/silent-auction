(ns silent-auction.handler
  (:use compojure.core
        [ring.middleware.json :only [wrap-json-response]]
        hiccup.bootstrap.middleware)
  (:require [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.urls :as urls]
            [silent-auction.views.core :as views]
            [silent-auction.models.db :as db]
            [silent-auction.models.items :as items]))

(defroutes admin-routes
  (GET "/item/new" []
    (views/create-item-modal))
  (GET "/item/:id" [id]
    (views/edit-item-modal (db/select-item id)))
  (POST "/item/" {params :params}
    (if-let [errors (items/validate params)]
      (-> (response/response errors)
        (assoc :status 400))
      (do
        (db/insert :items [(items/parse params)])
        (response/response {}))))
  (POST "/item/:id" {params :params}
    (if-let [errors (items/validate params)]
      (-> (response/response errors)
        (assoc :status 400))
      (do
        (db/update-values :items
                          ["id = ?" (Integer/parseInt (:id params))]
                          (items/parse params))
        (response/response {}))))
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
