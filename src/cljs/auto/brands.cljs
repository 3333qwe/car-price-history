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
    (html
      [:div.col-lg-2.col-sm-3.text-center
       [:a {:href (str "#/brand/" (:id item))}
        [:img.img-circle.img-responsive.img-center {:src (str "/img/" (:id item) ".gif") :alt (:name item)}]]
       [:h3 (:name item)]])))

(defcomponent brands-list [data owner]
  (render [_]
    (html
      [:div.container
       [:div.row
         [:div.col-lg-12
           [:h1.page-header "История цен на новые автомобили"]
           [:p "Сервис предоставляет возможность посмотреть историю цен на автомобиль выбранной марки и комплектации"]]]
       [:div.row
         [:div.col-lg-12
          [:h2.page-header "Марки"]]
         (map #(om/build brand-block {:data data :item %}) (:brands data))]])))