(ns entrance-plus.systems
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [entrance-plus.routes :refer [hello-routes site]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults
                                              api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [web.systems :as web]
            [voidwalker.systems :as voidwalker]
            [snow.systems :as system]
            (system.components
             [immutant-web :refer [new-immutant-web]]
             [endpoint :refer [new-endpoint]]
             [middleware :refer [new-middleware]]
             [repl-server :refer [new-repl-server]]
             [postgres :refer [new-postgres-database]]
             [handler :refer [new-handler]])))

(def rest-middleware
  (fn [handler]
    (wrap-restful-format handler
                         :formats [:json-kw]
                         :response-options {:json-kw {:pretty true}})))

(defn dev-system []
  (system/gen-system system-config))

(defn prod-system
  "Assembles and returns components for a production deployment"
  [config]
  (merge (dev-system)
         (component/system-map
          :repl-server (new-repl-server (read-string (config :repl-port))))))
