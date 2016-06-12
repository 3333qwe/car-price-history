(ns auto.brands
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [auto.models :as models]
            [cljs.core.async :refer [<!]]))

(defcomponent brand-block [{:keys [data item]} owner]
  (render [_]
    (html [:div.col-lg-2
           [:img {:src (str "/img/" (:id item) ".gif")}]
           [:div [:a {:href (str "#/brand/" (:id item))} (:name item)]]])))

(defcomponent brands-list [data owner]
  (render [_]
    (html
      [:div.hero-unit
       [:h1 "Производители"]
       [:div.row (map #(om/build brand-block {:data data :item %}) (:brands data))]
       ])))