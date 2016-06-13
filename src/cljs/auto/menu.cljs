(ns auto.menu
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]))

(defcomponent menu-item [{:keys [path name]} owner]
  (render [_]
    (html
      [:li
       [:a {:href (str "#" path)} name]])))

(defcomponent menu [app owner]
  (render [_]
    (html
      [:div.container
      [:div.navbar-header
        [:a.navbar-brand {:href "#"} "Car Price History"]]
      [:div.collapse.navbar-collapse {:id "navbar-collapse-1"}
       [:ul.nav.navbar-nav (om/build-all menu-item app)]]])))