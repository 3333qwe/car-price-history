(ns auto.lines
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [auto.api :as api]
            [cljs.core.async :refer [<!]]))

(defcomponent line-link [{:keys [l line-id]} owner]
  (render [_]
    (html [:a.list-group-item {:href  (str "#/brand/" (:brand_id l) "/model/" (:model_id l) "/line/" (:id l))
                               :class (if (= (int line-id) (:id l)) "active")}
           (str (:engine l) " (" (:hp l) "л.с.) " (:engine_type l) " " (:transmission l) " " (:drive l))])))

(defcomponent lines-list [data owner]
  (render-state [_ state]
    (html
      (if (zero? (count (:lines data)))
        [:div.well.well-sm "Нет данных"]
        [:div.list-group (map #(om/build line-link {:l % :line-id (:line-id state)}) (:lines data))]))))