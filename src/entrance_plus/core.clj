(ns entrance-plus.core
  (:gen-class)
  (:require  [voidwalker.systems :as voidwalker]
             [web.systems :as web]
             [snow.repl :as r]
             [clojure.spec.alpha :as s]))

(def system-config #{web/system-config voidwalker/system-config})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting machines")
  (r/start! system-config))
