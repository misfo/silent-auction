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
        (for [{category :name} (db/categories)]
          [:li [:a {:href "#"} category]])]]]]]))

(defn layout [& content]
  (html5
   [:head
    [:title "CHA Silent Auction"]
    (include-bootstrap)
    (include-js "/js/jquery-1.7.2.min.js"
                "/js/bootstrap-modal.js"
                "/js/silent-auction.js")
    (include-css "/css/silent-auction.css")]
   [:body
    (navbar)
    [:div.container content]]))

(defn item [{:keys [title]}]
  [:div.row.item
   [:div.span4
    [:div.thumbnail
     [:img {:src "http://placehold.it/360x286"}]
     [:div.caption "Photo by Patches O'Houlihan"]]]
   [:div.span8
    [:h2 title]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."]
    [:p "Donated by " [:strong "President Obama"]]
    [:p "Estimated market value: " [:strong "$2,000"]]
    [:p [:small "Please don't read this fineprint. Nothing to see here."]]]])

(defn item-category [itms]
  [:section
   [:div.page-header [:h1 (:category_name (first itms))]]
   (map item itms)])

(defn- category-option [{:keys [id name]}]
  [:option {:value id} name])

(defn edit-item-modal [it]
  (let [new-item (empty? it)]
    [[:div.modal-header
      [:button.close {:type "button", :data-dismiss "modal"} "×"]
      [:h3 (if new-item "Create Item" "Edit Item")]]
     [:div.modal-body
      [:form.form-horizontal
       [:fieldset
        (control-group "Title"
          [:input.input-xlarge {:type "text"
                                :name :title
                                :value (:title it)}])
        (control-group "Item ID"
          [:input.input-xlarge {:type "text"
                                :name "item_id"
                                :value (:item_id it)}])
        (control-group "Category"
          [:select (map category-option (db/categories))])
        (control-group "Description"
          [:textarea.input-xxlarge {:name "description"
                                    :rows 4
                                    :value (:description it)}])
        (control-group "Donor"
          [:input.input-xlarge {:type "text"
                                :name "donor"
                                :value (:donor it)}])
        (control-group "Estimated Market Value"
          [:input.input-xlarge {:type "text"
                                :name "estimated_market_value"
                                :value (:estimated_market_value it)}])
        (control-group "Fineprint"
          [:textarea.input-xxlarge {:name "fineprint"
                                    :rows 2
                                    :value (:fineprint it)}])]]]
     [:div.modal-footer
      [:a.btn {:data-dismiss "modal"} "Close"]
      [:a.btn.btn-primary (if new-item "Create Item" "Save Changes")]]]))

(defn create-item-modal []
  (edit-item-modal {}))

(defn items [itms]
  (let [itms-by-category (partition-by :category_name itms)]
    (layout
     [:button#create-item.btn.btn-primary "Create New Item"]
     (into [:div#modal.modal.hide] (create-item-modal))
     (map item-category itms-by-category))))

(defn- control-group
  [label & controls]
  [:div.control-group
   [:label.control-label label]
   (into [:div.controls] controls)])

