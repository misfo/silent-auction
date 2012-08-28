(ns silent-auction.urls)

(def admin-root "/admin")

(def login  "/login")
(def logout "/logout")

(defn new-item     []   (str admin-root "/item/new"))
(defn edit-item    [id] (str admin-root "/item/" id))
(defn upload-image [id] (str (edit-item id) "/upload"))
(defn delete-item  [id] (str (edit-item id) "/delete"))

