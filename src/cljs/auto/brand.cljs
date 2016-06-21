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

(defcomponent brand-view [{:keys [state] :as data} owner]
  (will-mount [_]
    (om/transact! data #(assoc % :app-mounted owner)))
  (will-unmount [_]
    (om/transact! data #(assoc % :app-mounted false)))
  (render [_]
    (html [:div.container
           [:div.row
            [:div.col-md-12
             [:h2
              [:img.brand-icon {:src (str "/img/" (:id (:brand data)) ".gif")}]
              (:name (:brand data))]]
            [:div.col-md-2.col-sm-4.models {:id "models-list"}
             (om/build models/models-list data)]
            (if (contains? state :model-id)
             [:div.col-md-3.col-sm-8 {:id "lines-list"}
              (om/build lines/lines-list data)])
            (if (contains? state :line-id)
             [:div.col-md-7.col-sm-12
              [:div (om/build chart/chart data)]
              [:div {:id "offers-list"}
               (om/build offers/offers-list data)]])]])))