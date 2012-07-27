(ns silent-auction.urls)

(def admin-root "/admin")

(defn edit-item   [id] (str admin-root "/item/" id))
(defn delete-item [id] (str (edit-item id) "/delete"))
