(ns silent-auction.handler
  (:use compojure.core
        hiccup.bootstrap.middleware)
  (:require [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [silent-auction.views.admin :as admin-views]
            [silent-auction.models.db :as db]))

(def admin-root "/admin")

(defroutes admin-routes
  (GET "/" [] (db/select-items admin-views/items)))

(defroutes app-routes
  (GET "/" [] (response/redirect admin-root))
  (context admin-root [] admin-routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    handler/site))
