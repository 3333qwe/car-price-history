(ns auto.db
  (:require [clojure.java.jdbc :as j]
            [environ.core :refer [env]]
            [clojure.core.async :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(def db-spec {:subprotocol "postgresql"
              :subname     "//127.0.0.1:5432/auto"
              :user        "webapp"
              :password    "qwe123asd"})

(defn insert! [table & values]
  (let [res (chan)]
    (go (>! res (apply j/insert! db-spec table values)))
    res))

(defn query [params & fields]
  (let [res (chan)]
    (go (>! res (apply j/query db-spec params fields)))
    res))
