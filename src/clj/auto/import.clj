(ns auto.import
  (:require [clojure.data.json :as json]
            [auto.db :as db]
            [auto.request :refer :all]
            [clojure.core.async :as a
             :refer [>! <! >!! <!! go go-loop chan buffer close! thread
                     alts! alts!! timeout]]))


(defn import-brand [{id "id" name "name" nameRu "ru_name"}]
  (let [brand (<!! (db/query ["SELECT id FROM brands WHERE market_id = ?" (Integer. id)]))]
    (:id (first (if (zero? (count brand))
                  (<!! (db/insert! :brands {:name name :name_ru nameRu :market_id (Integer. id)}))
                  brand)))))

(defn import-model [{id "id" name "name" nameRu "ru_name"} brandId]
  (let [models (<!! (db/query ["SELECT id FROM models WHERE market_id = ?" (Integer. id)]))
        ru (if-not (empty? nameRu) nameRu "")]
    (:id (first (if (zero? (count models))
                  (<!! (db/insert! :models {:name name :name_ru ru :market_id (Integer. id) :brand_id brandId}))
                  models)))))

(defn import-line [offer]
  (let [models (<!! (db/query ["SELECT * FROM models WHERE market_id = ?" (offer "id_model")]))]
    (if (zero? (count models)) (throw (Exception. "Model not found")))
    (let [model (first models)
          et (offer "engine_type")
          select-params {:model_id     (:id model)
                        :brand_id     (:brand_id model)
                        :engine       (offer "volume")
                        :drive        (offer "drive")
                        :engine_type  (if (clojure.string/blank? et) "бензин" et)
                        :transmission (offer "trans")}
          sql (str "SELECT * FROM lines WHERE " (clojure.string/join " AND " (map #(str (name %) " = ?") (keys select-params))))
          line (<!! (db/query (vec (concat [sql] (vals select-params)))))
          params (assoc select-params :hp (offer "power"))]
      (first (if-not (zero? (count line))
               line
               (<!! (db/insert! :lines params)))))))

(defn import-offer [offer]
  (let [line (import-line offer)
        offer-id (str (offer "id"))
        price (int (offer "price"))
        photo (offer "photo")
        offers (<!! (db/query ["SELECT * FROM offers WHERE type = 'av' AND ext_id = ? AND price = ?" offer-id price]))]
    (first (if-not (zero? (count offers))
             offers
             (<!! (db/insert! :offers {:line_id    (:id line)
                                       :line       ""
                                       :price      price
                                       :type       "av"
                                       :ext_id     offer-id
                                       :url        (str "http://avtomarket.ru/sale/" (offer "brand_name") "/" (offer "model_name") "/" (offer "id"))
                                       :img        (if photo (str "http://avtomarket.ru/stuff/oi/" photo) nil)
                                       :data       (json/write-str offer)
                                       :created_at (quot (System/currentTimeMillis) 1000)}))))))

(defn sync-offers []
  (doseq [brand (request-brands)]
    (let [brand-id (import-brand brand)]
      (println "Syncing brand " (brand "id"))
      (doseq [model (request-models (brand "id"))]
        (import-model model brand-id))
      (go (doseq [offer (<! (request-offers (brand "id")))]
            (import-offer offer)))
      (save-brand-logo (brand "id") brand-id))))