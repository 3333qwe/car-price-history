(ns auto.api
  (:require [ajax.core :refer [GET POST PUT]]
            [cljs.core.async :refer [put! chan <!]]))

(defn call
  ([method url]
   (call method url {}))
  ([method url body]
   (let [ch (chan)]
     (method (str url) (merge {:format          :json
                               :response-format :json
                               :keywords?       true
                               :handler         #(put! ch %)}
                              body))
     ch)))

(defn get-brands
  []
  (call GET "/v1/brands"))

(defn get-models
  [brand-id]
  (call GET (str "/v1/models/" brand-id)))

(defn get-lines
  [line-id]
  (call GET (str "/v1/lines/" line-id)))

(defn get-offers
  [line-id]
  (call GET (str "/v1/offers/" line-id)))