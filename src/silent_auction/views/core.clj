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

(defn- paragraphs [text]
  (for [p (str/split text #"(\n\s*){2,}")]
    [:p p]))

(defn item [{:keys [id title description donor estimated_market_value fineprint]}]
  [:div.row.item
   [:div.span4
    [:div.thumbnail
     [:img {:src "http://placehold.it/360x286"}]
     [:div.caption "Photo by Patches O'Houlihan"]]]
   [:div.span8
    [:h2 title
         "&nbsp;"
         [:a.btn.btn-danger.delete-item {:href (urls/delete-item id)} "Delete"]]
    (paragraphs description)
    [:p "Donated by " [:strong donor]]
    (when estimated_market_value
      [:p "Estimated market value: " [:strong (str "$" estimated_market_value)]])
    [:p [:small fineprint]]]])

(defn item-category [itms]
  [:section
   [:div.page-header [:h1 (:category_name (first itms))]]
   (map item itms)])

(defn- category-option [{:keys [id name]}]
  [:option {:value id} name])

(defn- control-group
  [label & controls]
  [:div.control-group
   [:label.control-label label]
   (into [:div.controls] controls)])

(defn edit-item-modal [it]
  (let [new-item (empty? it)]
    (list
     [:div.modal-header
      [:button.close {:type "button", :data-dismiss "modal"} "×"]
      [:h3 (if new-item "Create Item" "Edit Item")]]
     [:div.modal-body
      [:form.form-horizontal {:method "POST" :action (urls/edit-item (:id it))}
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
          [:select {:name "category_id"} (map category-option (db/categories))])
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
      [:a.btn.btn-primary.save (if new-item "Create Item" "Save Changes")]])))

(defn create-item-modal []
  (edit-item-modal {}))

(defn items [itms]
  (let [itms-by-category (partition-by :category_name itms)]
    (layout
     [:button#create-item.btn.btn-primary "Create New Item"]
     [:div#modal.modal.hide (create-item-modal)]
     (map item-category itms-by-category))))

