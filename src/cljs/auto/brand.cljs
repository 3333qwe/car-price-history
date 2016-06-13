(ns auto.brand
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [auto.models :as models]
            [auto.lines :as lines]
            [auto.offers :as offers]
            [auto.chart :as chart]
            [cljs.core.async :refer [<!]]))

(defcomponent brand-view [data owner]
  (did-mount [_]
    (if (contains? @data :offers)
      (chart/reder (:offers @data))))
  (render-state [_ state]
    (html [:div.container
           [:div.row
            [:div.col-md-12
             [:h2
              [:img.brand-icon {:src (str "/img/" (:id (:brand data)) ".gif")}]
              (:name (:brand data))]]
            [:div.col-md-2.col-sm-4.models (om/build models/models-list data {:init-state state})]
            (if (contains? state :model-id)
             [:div.col-md-3.col-sm-8 {:id "lines-list"}
              (om/build lines/lines-list data {:init-state state})])
            (if (contains? state :line-id)
             [:div.col-md-7.col-sm-12 {:id "offers-list"}
              [:div.chart {:id "chart-container"}]
              (om/build offers/offers-list data {:init-state state})])]])))