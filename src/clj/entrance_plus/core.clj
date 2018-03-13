(ns entrance-plus.core
  (:gen-class)
  (:require [system.repl :refer [set-init! start]]
            [voidwalker.systems :as void]
            [web.systems :as web]
            [entrance-plus.systems :as system]
            [clojure.spec.alpha :as s]
            [snow.systems :as sysutil]
            [com.stuartsierra.component :as component]))

;;todod next
;; write spec for system-config
;; add init-fn
;; add start fn with id and for all
;; add stop fn with id and for all
;; separate this into snow

(require '[pyro.printer :as printer])
(printer/swap-stacktrace-engine!)

;; (do
;;   (require '[orchestra.spec.test :as st])
;;   (st/instrument))

(def systems (atom ()))

 ;; (sysutil/stop-systems @systems)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting machines")
  (reset! systems (sysutil/start-systems [{::sysutil/system-fn void/system-config
                                           ::sysutil/config {:dbuser "void"
                                                             :db "voidwalker"
                                                             :password "walker"
                                                             :host "db"
                                                             :repl-port "8001"
                                                             :http-port "8000"}}
                                          {::sysutil/system-fn web/system-config
                                           ::sysutil/config {:http-port "7000"
                                                             :repl-port "7001"
                                                             :dbuser "void"
                                                             :db "voidwalker"
                                                             :password "walker"
                                                             :host "db"}}]
                                         :prod? true)))
