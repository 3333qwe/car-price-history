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
      [:div [:ul.nav.menu (om/build-all menu-item app)]])))