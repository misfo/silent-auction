(ns silent-auction.views.core
  (:use hiccup.core
        hiccup.page
        hiccup.bootstrap.element
        hiccup.bootstrap.page)
  (:require [clojure.string :as str]
            [silent-auction.models.db :as db]
            [silent-auction.urls :as urls]))

(defn- category-id [cat-name]
  (-> cat-name
    (str/replace #"\W+" "-")
    str/lower-case))

(defn navbar []
  (html
   [:div.navbar.navbar-fixed-top
    [:div.navbar-inner
     [:div.container
      [:a.brand {:href "/"} "CHA Silent Auction"]
      [:div.nav-collapse
       [:ul.nav
        (for [{cat :name} (db/used-categories)]
          [:li [:a {:href (str "#" (category-id cat))} cat]])]]]]]))

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
         [:a.btn.btn-primary.edit-item {:href (urls/edit-item id)} "Edit"]
         "&nbsp;"
         [:a.btn.btn-danger.delete-item {:href (urls/delete-item id)} "Delete"]]
    (paragraphs description)
    [:p "Donated by " [:strong donor]]
    (when estimated_market_value
      [:p "Estimated market value: " [:strong (str "$" estimated_market_value)]])
    [:p [:small fineprint]]]])

(defn item-category [itms]
  (let [cat (:category_name (first itms))]
    [:section {:id (category-id cat)}
      [:div.page-header [:h1 cat]]
      (map item itms)]))

(defn- category-option [selected-id {:keys [id name]}]
  [:option {:value id :selected (= id selected-id)} name])

(defn- control-group
  [label & controls]
  [:div.control-group
   [:label.control-label label]
   (-> [:div.controls]
     (into controls)
     (conj [:span.help-inline]))])

(defn modal [title body buttons]
  (list
   [:div.modal-header
    [:button.close {:type "button", :data-dismiss "modal"} "×"]
    [:h3 title]]
   [:div.modal-body body]
   [:div.modal-footer
    [:a.btn {:data-dismiss "modal"} "Close"]
    buttons]))

(defn item-form [it]
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
       [:select {:name "category_id"} (map (partial category-option (:category_id it))
                                           (db/categories))])
     (control-group "Description"
       [:textarea.input-xxlarge {:name "description"
                                 :rows 4}
                                (:description it)])
     (control-group "Donor"
       [:input.input-xlarge {:type "text"
                             :name "donor"
                             :value (:donor it)}])
     (control-group "Estimated Market Value"
       [:div.input-prepend
        [:span.add-on "$"]
        [:input.input-xlarge {:type "text"
                              :name "estimated_market_value"
                              :value (:estimated_market_value it)}]])
     (control-group "Fineprint"
       [:textarea.input-xxlarge {:name "fineprint"
                                 :rows 2}
                                (:fineprint it)])]])

(defn edit-item-modal [it]
  (html
   (modal "Edit Item"
          (item-form it)
          [:a.btn.btn-primary.save "Save Changes"])))

(defn create-item-modal []
  (html
   (modal "Create Item"
          (item-form {})
          [:a.btn.btn-primary.save "Create Item"])))

(defn items [itms]
  (let [itms-by-category (partition-by :category_name itms)]
    (layout
     [:a#create-item.btn.btn-primary {:href (urls/new-item)} "Create New Item"]
     [:div#item-modal.modal.hide]
     (map item-category itms-by-category))))

