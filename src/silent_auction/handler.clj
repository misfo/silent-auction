(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.views.admin :as admin-views]
            [silent-auction.models.db :as db]))

(defroutes app-routes
  (GET "/" [] (db/select-items admin-views/items))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
