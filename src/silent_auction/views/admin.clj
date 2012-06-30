(ns silent-auction.views.admin
  (:use hiccup.core
        hiccup.page
        hiccup.bootstrap.element
        hiccup.bootstrap.page)
  (:require [clojure.string :as str]
            [silent-auction.models.db :as db]
            [silent-auction.urls :as urls]))

(defn navbar []
  (html
    [:div.navbar.navbar-fixed-top
      [:div.navbar-inner
        [:div.container
          [:a.btn.btn-navbar {:data-toggle "collapse", :data-target ".nav-collapse"}
            [:span.icon-bar]
            [:span.icon-bar]
            [:span.icon-bar]]
          [:a.brand {:href urls/admin-root} "CHA Silent Auction"]
          [:div.nav-collapse
            [:ul.nav
              [:li.active [:a {:href urls/admin-root} "Items"]]]]]]]))

(defn layout [& content]
  (html5
    [:head
      [:title "CHA Silent Auction admin"]
      (include-bootstrap)
      (include-css "/css/silent-auction.css")]
    [:body
      (navbar)
      [:div.container content]]))

(defn- city-state-zip
  [city state zip]
  (let [city-state (str/join ", " (remove nil? [city state]))]
    (str/join " " (remove str/blank? [city-state zip]))))

(defn- item-row [{:keys [id
                         complete
                         category
                         item
                         solicited-by
                         fair-market-value]}]
  [complete
   category
   item
   solicited-by
   fair-market-value
   (list
     [:p [:a.btn.btn-small {:href (urls/edit-item id)} [:i.icon-pencil] " Edit"]]
     [:p [:a.btn.btn-small {:href "#"} [:i.icon-arrow-up] " Upload"]])])

(defn items [itms]
  (layout
    [:div.page-header [:h1 "Auction Items"]]
    (table :styles #{:bordered :striped}
           :head ["Item Complete?"
                  "Category"
                  "Item"
                  "Solicited by"
                  "Fair Mkt. Value"
                  "Actions"]
           :body (map item-row itms))))

(defn- control-group
  [label & controls]
  [:div.control-group
    [:label.control-label label]
    (into [:div.controls] controls)])

(defn edit-item [it]
  (layout
    [:h2 "Edit Item"]
    [:form.form-horizontal
      [:fieldset
        (control-group "Item"
          [:input.input-xlarge {:type "text" :value (:item it)}])
        (control-group "Item Complete?"
          [:input.input-xlarge {:type "text" :value (:complete it)}])
        (control-group "Category"
          [:select (map #(vector :option %) db/categories)])
        (control-group "Solicited by"
          [:input.input-xlarge {:type "text" :value (:solicited-by it)}])
        (control-group "Fair Market Value"
          [:input.input-xlarge {:type "text" :value (:fair-market-value it)}])
        ]]))
