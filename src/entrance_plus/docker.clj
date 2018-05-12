(ns entrance-plus.docker
  (:require [clojure.string :as str]
            [selmer.parser :as selmer]
            [clojure.java.io :as io])
  (:import (com.spotify.docker.client DockerClient
                                      DockerClient$ListContainersParam
                                      DockerClient$ExecCreateParam
                                      DockerClient$ExecStartParameter
                                      DockerClient$EventsParam
                                      DefaultDockerClient)))

(require '[cognitect.transcriptor :as xr :refer (check!)])

(defn new-docker []
  (.build (DefaultDockerClient/fromEnv)))

(defn list-containers [docker]
  (.listContainers docker (make-array DockerClient$ListContainersParam 0)))

(defn exec-command [docker cmd container]
  (let [exec-creation (.execCreate docker (.id container)
                                          (into-array String cmd)
                                          (into-array DockerClient$ExecCreateParam
                                            [(DockerClient$ExecCreateParam/attachStdout)
                                             (DockerClient$ExecCreateParam/attachStderr)]))]
    (-> docker
      (.execStart (.id exec-creation) (into-array DockerClient$ExecStartParameter nil))
      .readFully)))

(defn inspect-container [docker container]
  (.inspectContainer docker (.id container)))

(defn get-ip [docker container]
  (->> container
       (inspect-container docker)
       .networkSettings
       .networks
       first
       .getValue
       .ipAddress))


(defn parse-env-key [key]
  (str/lower-case (str/join "" (rest (str/split key #"_")))))

(defn parse-env-value [value]
  (let [[host port] (str/split value #":")]
    {:host host
     :port port}))

(defn parse-env [env]
  (parse-env-value (second (str/split env #"="))))


(defn get-env [docker container]
  (->> container
       (inspect-container docker)
       .config
       .env
       (filter (fn [env]
                 (str/starts-with? env "APP")))
       (map parse-env)))


(def stream (-> (new-docker)
                (.events (into-array DockerClient$EventsParam nil))))

(.next stream)

(defn read-nginx []
  (-> "nginx.conf"
      io/resource
      slurp))

(defn write-nginx [contents]
  (spit "resources/default.conf" contents))

(defn collect-app-data [docker container]
  (map (fn [env-data]
         (merge env-data
           {:ip (get-ip docker container)}))
    (get-env docker container)))

(defn gen-nginx-config []
  (let [docker (new-docker)
        container (-> docker list-containers first)
        apps (collect-app-data docker container)]
       (-> (read-nginx)
           (selmer/render {:apps apps})
           write-nginx)))
