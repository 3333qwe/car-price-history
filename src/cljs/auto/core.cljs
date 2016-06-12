(ns auto.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [secretary.core :as secretary :refer-macros [defroute]]
            [auto.menu :as menu]
            [auto.api :as api]
            [auto.brands :as brands]
            [auto.brand :as brand]
            [auto.models :as models]
            [cljs.core.async :refer [<!]])
  (:import goog.History
           goog.history.EventType))

; Allows using print/println for browser console logging
(enable-console-print!)

(def history (History.))

(def menu-state
  (atom [{:name "Dashboard" :path "/"}
         {:name "Articles" :path "/articles"}]))

; State of the page with all brands
(def app-state (atom {}))
; State of the page with specific brand
(def brand-state (atom {}))

(defn render-brand [params]
  (go
    (if-not (contains? brand-state :brand)
      (let [brand-models (<! (api/get-models (:brand-id params)))]
        (reset! brand-state brand-models)))
    (if (and (contains? params :model-id))
      (let [lines (<! (api/get-lines (:model-id params)))]
        (swap! brand-state assoc :lines lines)))
    (if (and (contains? params :line-id))
      (let [offers (<! (api/get-offers (:line-id params)))]
        (swap! brand-state assoc :offers offers)))
    (om/root brand/brand-view brand-state
             {:target     (. js/document (getElementById "app-content"))
              :init-state params})))

(defroute "/" []
          (go (let [brands (<! (api/get-brands))]
                (reset! app-state {:brands brands})))
          (om/root brands/brands-list app-state
                   {:target (. js/document (getElementById "app-content"))}))

(defroute "/brand/:brand-id" {:as params}
          (render-brand params))

(defroute "/brand/:brand-id/model/:model-id" {:as params}
          (render-brand params))

(defroute "/brand/:brand-id/model/:model-id/line/:line-id" {:as params}
          (render-brand params))

(doto history
  (goog.events/listen EventType.NAVIGATE #(secretary/dispatch! (.-token %)))
  (.setEnabled true))

(defn main []
  (om/root
    menu/menu
    menu-state
    {:target (. js/document (getElementById "app-menu"))}))

;(main)