(ns silent-auction.views.admin
  (:use hiccup.core
        hiccup.page
        hiccup.bootstrap.page))

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
      [:title "Bootstrapped Example"]
      (include-bootstrap)
      [:style "body {padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */ }"]]
    [:body
      (navbar)
      [:div.container content]]))

(defn item-row [{:keys [complete
                        category
                        item
                        description
                        donor-name
                        name-for-listing
                        address
                        city
                        state
                        zip
                        solicited-by
                        fair-market-value]}]
  [:tr
    [:td complete]
    [:td category]
    [:td item]
    [:td description]
    [:td donor-name]
    [:td name-for-listing]
    [:td address]
    [:td (str city ", " state " " zip)]
    [:td solicited-by]
    [:td fair-market-value]])

(defn items [itms]
  (layout
    [:div.page-header [:h1 "Auction Items"]]
    [:table.table.table-bordered.table-striped
      [:thead
        [:tr
          [:th "Item Complete?"]
          [:th "Category"]
          [:th "Item"]
          [:th "Description"]
          [:th "Donor Name"]
          [:th "Name for Listing"]
          [:th "Address"]
          [:th "City, State, Zip"]
          [:th "Solicited by"]
          [:th "Fair Mkt. Value"]]]
      [:tbody
        (map item-row itms)]]))
