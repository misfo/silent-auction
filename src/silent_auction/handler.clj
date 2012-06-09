(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.views.admin :as admin-views]
            [silent-auction.models.items :as items]))

(defroutes app-routes
  (GET "/" [] (admin-views/items (items/all)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
