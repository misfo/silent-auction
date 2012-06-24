(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.urls :as urls]
            [silent-auction.views.admin :as admin-views]
            [silent-auction.models.db :as db]))

(defroutes admin-routes
  (GET "/" [] (db/select-items admin-views/items)))

(defroutes app-routes
  (GET "/" [] (response/redirect urls/admin-root))
  (context urls/admin-root [] admin-routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
