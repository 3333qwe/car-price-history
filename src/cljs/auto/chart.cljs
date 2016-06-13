(ns auto.chart)

(defn add-rows [rows]
  (js/console.log (pr-str rows))
  (let [data (js/google.visualization.DataTable.)]
    (.addColumn data "date" "Дата")
    (.addColumn data "number" "Цена")
    (.addRows data (clj->js (vec (map #(vec [(js/Date. (* (:created_at %) 1000)) (:price %)]) rows))))
    data))

(defn chart-options []
  (clj->js {:title  "Изменение цены на комплектацию"
            :width 400
            :height 300}))

(defn get-chart []
  (js/google.visualization.LineChart. (. js/document (getElementById "chart-container"))))

(defn draw-chart [data]
  (js/console.log "123")
  (let [data (add-rows data)
        options (chart-options)
        chart (get-chart)]
    (.draw chart data options)))

(def loaded (atom false))

(defn reder [data]
  (if-not @loaded
    (do
      (.load js/google.charts "visualization" "1.0" (clj->js {:packages ["corechart"]}))
      (.setOnLoadCallback js/google.charts (fn [] (draw-chart data)))
      (reset! loaded true))
    (draw-chart data)))