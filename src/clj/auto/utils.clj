(ns auto.utils
  (:import (java.util Calendar)))

(defn now []
  (quot (System/currentTimeMillis) 1000))

(defn get-date-timestamp []
  (let [c (Calendar/getInstance)]
    (.set c Calendar/HOUR_OF_DAY 0)
    (.set c Calendar/MINUTE 0)
    (.set c Calendar/SECOND 0)
    (.set c Calendar/MILLISECOND 0)
    (quot (.getTimeInMillis c) 1000)))