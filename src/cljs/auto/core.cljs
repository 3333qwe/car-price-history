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
  (atom []))

; State of the application
(def app-state (atom {}))

(defn app [params]
  (go
    (let [brand-models (<! (api/get-models (:brand-id params)))]
      (reset! app-state brand-models))
    (if (contains? params :model-id)
      (let [lines (<! (api/get-lines (:model-id params)))]
        (swap! app-state assoc :lines lines)))
    (if (contains? params :line-id)
      (let [offers (<! (api/get-offers (:line-id params)))]
        (swap! app-state assoc :offers offers)))
    (om/root brand/brand-view app-state
             {:target     (. js/document (getElementById "app-content"))
              :init-state params})))

(defn scroll-to [element-id]
  (let [element (. js/document (getElementById element-id))
        position (.getBoundingClientRect element)]
    (if-not (zero? (:top position)) (.scrollIntoView element))))

(defroute "/" []
          (go (let [brands (<! (api/get-brands))]
                (reset! app-state {:brands brands}))
              (om/root brands/brands-list app-state
                       {:target (. js/document (getElementById "app-content"))})))

(defroute "/brand/:brand-id" {:as params}
          (js/scroll 0 0)
          (app params))

(defroute "/brand/:brand-id/model/:model-id" {:as params}
          (go
            (<! (app params))
            (scroll-to "lines-list")))

(defroute "/brand/:brand-id/model/:model-id/line/:line-id" {:as params}
          (go
            (<! (app params))
            (scroll-to "offers-list")))

(doto history
  (goog.events/listen EventType.NAVIGATE #(secretary/dispatch! (.-token %)))
  (.setEnabled true))

(defn main []
  (om/root
    menu/menu
    menu-state
    {:target (. js/document (getElementById "app-menu"))}))

(main)