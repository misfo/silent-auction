(ns silent-auction.urls)

(def admin-root "/admin")

(defn new-item    []   (str admin-root "/item/new"))
(defn edit-item   [id] (str admin-root "/item/" id))
(defn delete-item [id] (str (edit-item id) "/delete"))
