(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.urls :as urls]
            [silent-auction.views.core :as views]
            [silent-auction.models.db :as db]))

(defroutes admin-routes
  (GET "/item/:id" [id] (views/edit-item (db/select-item id))))

(defroutes app-routes
  (GET "/" [] (views/items []))
  (context urls/admin-root [] admin-routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
