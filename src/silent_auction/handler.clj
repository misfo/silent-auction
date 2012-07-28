(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.urls :as urls]
            [silent-auction.views.core :as views]
            [silent-auction.models.db :as db]
            [silent-auction.models.items :as items]))

(defroutes admin-routes
  (GET "/item/:id" [id]
    (views/edit-item-modal (db/select-item id)))
  (POST "/item/" {params :params}
    (db/insert :items [(items/parse params)])
    (response/redirect "/"))
  (POST "/item/:id" [id]
    (println id))
  (POST "/item/:id/delete" [id]
    (db/delete-rows :items ["id = ?" (Integer/parseInt id)])
    ""))

(defroutes app-routes
  (GET "/" [] (views/items (db/select-items)))
  (context urls/admin-root [] admin-routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
