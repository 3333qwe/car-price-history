(ns auto.offers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [cljs.core.async :refer [<!]]))

(defcomponent offer-link [{:keys [offer]} owner]
  (render [_]
    (html [:tr
           [:td (.toLocaleString (js/Date. (* (:created_at offer) 1000)) "ru-RU")]
           [:td [:a {:href (:url offer) :target "_blank"} (str (.toLocaleString (:price offer) "ru-RU" #js {:style "currency" :currency "RUB"}))]]])))

(defcomponent offers-list [data owner]
  (render [_]
    (html
      [:div.row
       (if (zero? (count (:offers data)))
         [:div.well.well-sm "Нет предложений"]
          [:table {:class "table"}
            [:thead
             [:tr
              [:th "Дата обновления"]
              [:th "Стоимость"]]]
            [:tbody (map #(om/build offer-link {:offer %}) (:offers data))]])])))