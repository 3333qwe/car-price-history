(ns auto.core
  (:require [clojure.tools.cli :as cli]
            [auto.server :as server]
            [auto.import :as import]))

(def cli-options
  [["-t" "--task NAME" "Run task with given name"
    :default "server"]
   ["-h" "--help"]])

(defn -main [& args]
  (server/run))
