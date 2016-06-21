(ns auto.chart
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [cljs.core.async :refer [<!]]
            [auto.api :as api]))

(defn add-rows [rows]
  (let [data (js/google.visualization.DataTable.)]
    (.addColumn data "date" "Дата")
    (.addColumn data "number" "Цена")
    (.addRows data (clj->js (vec (map #(vec [(js/Date. (* (:date %) 1000)) (:price %)]) rows))))
    data))

(defn chart-options []
  (clj->js {:title  "Изменение цены на комплектацию"
            :width  "100%"
            :height 300}))

(defn get-chart []
  (js/google.visualization.LineChart. (. js/document (getElementById "chart-container"))))

(defn draw-chart [data]
  (let [data (add-rows data)
        options (chart-options)
        chart (get-chart)]
    (.draw chart data options)))

(def loaded (atom false))

(defn reder-chart [line-id]
  (go
    (let [data (<! (api/get-chart-data line-id))]
      (if @loaded
        (draw-chart data)
        (do
          (.load js/google.charts "visualization" "1.0" (clj->js {:packages ["corechart"] :language "ru"}))
          (.setOnLoadCallback js/google.charts (fn [] (draw-chart data)))
          (reset! loaded true))))))

(defcomponent chart [data owner]
  (did-mount [_]
    (reder-chart (:line-id (:state data))))
  (did-update [_ _ _]
    (reder-chart (:line-id (:state data))))
  (render [_]
    (html
     [:div.chart {:id "chart-container"}])))