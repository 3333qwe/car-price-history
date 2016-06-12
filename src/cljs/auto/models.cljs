(ns auto.models
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [cljs.core.async :refer [<!]]))

(defcomponent model-link [{:keys [model model-id]} owner]
  (render [_]
    (html [:li
           [:a {:href (str "#/brand/" (:brand_id model) "/model/" (:id model))
                :class (if (= (int model-id) (:id model)) "active")}
            (:name model)]])))

(defcomponent models-list [{:keys [models] :as data} owner]
  (render-state [_ state]
    (html
      [:div.row
       [:ul (map #(om/build model-link {:model % :model-id (:model-id state)}) models)]])))