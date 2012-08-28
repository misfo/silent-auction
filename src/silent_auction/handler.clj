(ns silent-auction.handler
  (:use [clojure.pprint :only [pprint]] compojure.core
        [ring.middleware.json :only [wrap-json-response]]
        hiccup.bootstrap.middleware)
  (:require [clojure.string :as str]
            [ring.util.response :as response]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
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

(defn load-user-creds
  [email]
  (if-let [user (db/select-user email)]
    {:username (:email user)
     :password (:crypted_password user)
     :roles #{:admin}}))

(def admin-app
  (-> admin-routes
    (friend/wrap-authorize #{:admin})
    wrap-json-response))

(defroutes app-routes
  (GET "/" r
    (let [identity (get-in r [:session :cemerick.friend/identity])]
      (views/items (db/select-items) (when identity (friend/current-authentication identity)))))
  (GET "/login" [] (views/login))
  (friend/logout (GET "/logout" [] (response/redirect "/")))
  (context urls/admin-root [] admin-app)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    wrap-bootstrap-resources
    (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn load-user-creds)
                          :workflows [(workflows/interactive-form)]
                          :login-uri "/login"
                          :default-landing-uri "/"})
    handler/site))
