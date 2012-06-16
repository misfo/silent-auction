(ns silent-auction.views.admin
  (:use hiccup.core
        hiccup.page
        hiccup.bootstrap.element
        hiccup.bootstrap.page)
  (:require [clojure.string :as str]))

(defn navbar []
  (html
    [:div.navbar.navbar-fixed-top
      [:div.navbar-inner
        [:div.container
          [:a.btn.btn-navbar {:data-toggle "collapse", :data-target ".nav-collapse"}
            [:span.icon-bar]
            [:span.icon-bar]
            [:span.icon-bar]]
          [:a.brand {:href "#"} "CHA Silent Auction"]
          [:div.nav-collapse
            [:ul.nav
              [:li.active [:a {:href "/"} "Items"]]]]]]]))

(defn layout [& content]
  (html5
    [:head
      [:title "CHA Silent Auction admin"]
      (include-bootstrap)
      [:style "body {padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */ }"]]
    [:body
      (navbar)
      [:div.container content]]))

(defn- city-state-zip
  [city state zip]
  (let [city-state (str/join ", " (remove nil? [city state]))]
    (str/join " " (remove str/blank? [city-state zip]))))

(defn- item-row [{:keys [complete
                         category
                         item
                         donor-name
                         name-for-listing
                         city
                         state
                         zip
                         solicited-by
                         fair-market-value]}]
  [complete
   category
   item
   donor-name
   name-for-listing
   (city-state-zip city state zip)
   solicited-by
   fair-market-value
   '([:p [:a.btn.btn-small {:href "#"} [:i.icon-pencil] " Edit"]]
     [:p [:a.btn.btn-small {:href "#"} [:i.icon-arrow-up] " Upload"]])])

(defn items [itms]
  (layout
    [:div.page-header [:h1 "Auction Items"]]
    (table :styles #{:bordered :striped}
           :head ["Item Complete?"
                  "Category"
                  "Item"
                  "Donor Name"
                  "Name for Listing"
                  "City, State, Zip"
                  "Solicited by"
                  "Fair Mkt. Value"
                  "Actions"]
           :body (map item-row itms))))
