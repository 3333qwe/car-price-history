(ns auto.request
  (:require [clojure.data.json :as json]
            [clojure.core.async
             :refer [>! <! >!! <!! go go-loop chan buffer close! thread
                     alts! alts!! timeout]]
            [org.httpkit.client :as http]
            [clojure.java.io :as io]))


(defn request-json-async [url]
  (let [res (chan)]
    (go (>! res (json/read-str (:body @(http/get url)))))
    res))

(defn request-json [url]
  (json/read-str (:body @(http/get url))))

(defn request-brands []
  ((request-json "http://avtomarket.ru/service/mobile/auto.class.getBrands/1/?section=sale&date=") "result"))

(defn request-models [brandId]
  ((request-json (str "http://avtomarket.ru/service/mobile/auto.brand.getModels/" brandId "/?section=sale&class=1&date=")) "result"))

(defn _req-offers [brandId page]
  (request-json-async
    (str "http://avtomarket.ru/service/mobile/auto.offer.list/?class=1&brand=" brandId "&year_from=2015&run_to=1000&sort=price&page=" page "&date=")))

(defn request-offers [brandId]
  (go-loop [page 0
            results []]
    (let [resp (<! (_req-offers brandId page))
          offers ((resp "result") "ordinary_offer_list")]
      (println page (count offers))
      (if-not (zero? (count offers))
        (recur (inc page)
               (into results offers))
        results))))

(defn save-to-file [uri file]
  (with-open [in (io/input-stream uri)
              out (io/output-stream file)]
    (io/copy in out)))

(defn save-brand-logo [av-brand-id our-brand-id]
  (doseq [ext ["gif" "jpg"]]
    (let [logo-url (str "http://avtomarket.ru/stuff/logo/brand/" av-brand-id "." ext)
          file-path (str (.getPath (io/resource "public")) "/img/" our-brand-id ".gif")]
      (when-not (.exists (io/as-file file-path))
        (try
          (save-to-file logo-url file-path)
          (catch Exception e ()))))))