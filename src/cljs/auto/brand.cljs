(ns auto.brand
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [auto.models :as models]
            [auto.lines :as lines]
            [auto.offers :as offers]
            [cljs.core.async :refer [<!]]))

(defcomponent brand-view [data owner]
  (render-state [_ state]
    (html [:div.row
           [:div.col-lg-2
             [:a {:href (str "#/")} (:name (:brand data))]
             [:img {:src (str "/img/" (:id (:brand data)) ".gif")}]
             [:div (om/build models/models-list data {:init-state state})]]
           (if (contains? state :model-id)
             [:div.col-lg-3 (om/build lines/lines-list data {:init-state state})])
           (if (contains? state :line-id)
             [:div.col-lg-7 (om/build offers/offers-list data {:init-state state})])])))