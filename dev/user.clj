(ns user
  (:require [clojure.spec.alpha :as s]
            [snow.repl :as repl]
            [snow.env :refer [read-edn]]
            [cider.nrepl :refer [cider-nrepl-handler]]
            
            [shadow.cljs.devtools.server :as server]
            [entrance-plus.core :refer [system-config]]
            [shadow.cljs.devtools.api :as shadow]))

;; (do (require '[expound.alpha :as expound])
;;     (set! s/*explain-out* expound/printer))

(def config (read-edn "profiles.edn"))

(defn cljs-repl []
  (cemerick.piggieback/cljs-repl :app))

(defn start! []
  (repl/start! system-config))

(defn restart-systems! []
  (do (repl/stop!)
      (repl/start! system-config)))

#_(shadow/release :app)

#_(Restart-systems!)

(defn -main [& args]
  (shadow/release :app)
  (start!))
