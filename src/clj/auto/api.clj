(ns auto.api
  (:require [clojure.data.json :as json]
            [clojure.core.async :refer [<!!]]
            [auto.db :as db]))

(defn get-brands
  [req]
  (json/write-str (<!! (db/query ["SELECT id, name, name_ru FROM brands ORDER BY name ASC"]))))

(defn get-models
  [brand-id]
  (let [id (Integer/parseInt brand-id)]
    (json/write-str {:brand  (first (<!! (db/query ["SELECT id, name, name_ru FROM brands WHERE id = ?" id])))
                     :models (<!! (db/query ["SELECT id, name, name_ru, brand_id FROM models WHERE brand_id = ? ORDER BY name" id]))})))
(defn get-lines
  [model-id]
  (json/write-str (<!! (db/query ["SELECT * FROM lines WHERE model_id = ? ORDER BY engine, hp" (Integer/parseInt model-id)]))))

(defn get-offers
  [params]
  (let [line-id (Integer/parseInt (:line-id params))
        limit (if (contains? params :limit) (:limit params) 100)
        offset (if (contains? params :offset) (:offset params) 0)]
    (json/write-str (<!! (db/query ["
      SELECT id, line_id, line, price, type, created_at, img, url
        FROM offers WHERE line_id = ? ORDER BY id DESC LIMIT ? OFFSET ?" line-id limit offset])))))