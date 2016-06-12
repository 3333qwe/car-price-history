(ns auto.offers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [cljs.core.async :refer [<!]]))

(defcomponent offer-link [{:keys [offer]} owner]
  (render [_]
    (html [:tr
           [:td (.toDateString (js/Date. (* (:created_at offer) 1000)))]
           [:td [:a {:href (:url offer) :target "_blank"} (str (.toLocaleString (:price offer) "ru-RU" #js {:style "currency" :currency "RUB"}))]]])))

(defcomponent offers-list [data owner]
  (render [_]
    (html
      [:div.row
       (if (zero? (count (:offers data)))
         [:p "Нет предложений"]
         [:table {:class "table"}
          [:tbody (map #(om/build offer-link {:offer %}) (:offers data))]])])))