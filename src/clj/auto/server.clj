(ns auto.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [auto.api :as api])
  (:gen-class))

(defroutes routes
           (GET "/v1/brands" [] api/get-brands)
           (GET "/v1/models/:brand-id" [brand-id] (api/get-models brand-id))
           (GET "/v1/lines/:model-id" [model-id] (api/get-lines model-id))
           (GET "/v1/offers/:line-id" {params :params} (api/get-offers params))
           (GET "/" _
             {:status  200
              :headers {"Content-Type" "text/html; charset=utf-8"}
              :body    (io/input-stream (io/resource "public/index.html"))})
           (resources "/"))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-with-logger
      wrap-gzip))

(defn run [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println "Server starting on port " port)
    (run-jetty http-handler {:port port :join? false})))
