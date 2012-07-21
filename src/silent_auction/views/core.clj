(ns silent-auction.views.core
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
          [:a.brand {:href "/"} "CHA Silent Auction"]
          [:div.nav-collapse
            [:ul.nav
              (for [category db/categories]
                [:li [:a {:href "#"} category]])]]]]]))

(defn layout [& content]
  (html5
    [:head
      [:title "CHA Silent Auction"]
      (include-bootstrap)
      (include-css "/css/silent-auction.css")]
    [:body
      (navbar)
      [:div.container content]]))

(defn item [it]
  [:div.row.item
   [:div.span4
    [:div.thumbnail
     [:img {:src "http://placehold.it/360x286"}]
     [:div.caption "Photo by Patches O'Houlihan"]]]
   [:div.span8
    [:h2 "Title for item"]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donated by " [:strong "President Obama"]]
    [:p "Estimated market value: " [:strong "$2,000"]]
    [:p [:small "Please don't read this fineprint. Nothing to see here."]]
   ]
      ])

(defn item-category [[name itms]]
  [:section
   [:div.page-header [:h1 name]]
   (map item [:fake :fake])
   ]
  )

(defn items [itms]
  (let [itms-by-category (reduce (fn [m c] (assoc m c [])) (sorted-map) db/categories)]
    (layout (map item-category itms-by-category))))

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
