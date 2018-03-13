(ns entrance-plus.app
  (:require [reagent.core :as r]
            [bide.core :as b]
            [entrance-plus.ui :refer [home-page about-page]]))

(enable-console-print!)

(defn transform-map [rmap]
  (into []
        (for [[r u] rmap]
          [u r])))

(defn make-bide-router [rmap]
  (b/router (transform-map rmap)))

(def route-map {:entrance-plus.home "/home"
                :entrance-plus.about "/about"})

(def router (make-bide-router route-map))

(defn nav-link [route text]
  [:a {:href (route route-map)
       :on-click #(do
                    (-> % .preventDefault)
                    (b/navigate! router route))} text])

(defn page [name]
  [:div
   [:nav
    [:div [nav-link :entrance-plus.home "Home"]]
    [:div [nav-link :entrance-plus.about "About"]]]
   (case name
     :entrance-plus.home [home-page]
     :entrance-plus.about [about-page])])


(defn on-navigate
  "A function which will be called on each route change."
  [name params query]
  (println "Route change to: " name params query)
  (r/render [page name]
            (js/document.getElementById "app")))

(defn ^:export run []
  (b/start! router {:default :entrance-plus.home
                    :html5? true
                    :on-navigate on-navigate}))
(run)

