(def project 'entrance-plus)
(def version "0.1.0-SNAPSHOT")

(def digital-ocean-access-token "63a091058d7247d2fcc5932971183b414ad0a8c610c1c34ce8c7a54fe2382744")

(set-env! :resource-paths #{"src/cljs" "src/clj" "resources"}
          ;; :checkouts '[[voidwalker "0.1.0-SNAPSHOT"]
          ;;              [snow "0.1.0-SNAPSHOT"]
          ;;              [entranceplus/web "0.1.0-SNAPSHOT"]]
          :dependencies   '[[org.clojure/clojure "1.9.0"]
                            [airplanes "0.0.1-SNAPSHOT"]
                            [org.clojure/clojurescript "1.9.946"]
                            [orchestra "2017.11.12-1"]
                            [org.clojure/test.check "0.10.0-alpha2"]
                            [org.immutant/immutant "2.1.9"]
                            [org.danielsz/system "0.4.2-SNAPSHOT"]
                            [org.clojure/java.jdbc "0.7.3"]
                            [org.clojure/tools.cli "0.3.5"]
                            [org.clojure/tools.logging "0.4.0"]
                            [metosin/ring-http-response "0.9.0"]
                            [compojure "1.6.0"]
                            [environ "1.1.0"]
                            [boot-environ "1.1.0"]
                            [ring "1.6.3"]
                            [org.clojure/tools.nrepl "0.2.12"]
                            [ring/ring-defaults "0.3.1"]
                            [ring-middleware-format "0.7.2"]
                            [adzerk/boot-reload "0.5.2" :scope "test"]
                            [adzerk/boot-test "1.2.0" :scope "test"]
                            [reagent "0.8.0-alpha2"]
                            [reagi "0.10.1"]
                            [funcool/bide "1.6.0"]
                            [proto-repl "0.3.1"]
                            [voidwalker "0.1.0-SNAPSHOT"]
                            [adzerk/boot-reload "0.5.2" :scope "test"]
                            [adzerk/boot-test "1.2.0" :scope "test"]
                            [adzerk/boot-cljs "2.1.4" :scope "test"]
                            [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                            [adzerk/boot-test "1.2.0" :scope "test"]
                            [adzerk/boot-reload "0.5.2" :scope "test"]
                            [com.cemerick/piggieback "0.2.1" :scope "test"]
                            [binaryage/devtools "0.9.4" :scope "test"]
                            [snow "0.1.0-SNAPSHOT"]
                            [venantius/pyro "0.1.1"]
                            [com.cognitect/transcriptor "0.1.5"]
                            [org.clojure/core.async "0.4.474"]
                            [com.fasterxml.jackson.core/jackson-core "2.9.4"]
                            [com.spotify/docker-client "8.11.1"]
                            [selmer "1.11.7"]
                            [entranceplus/web "0.1.0-SNAPSHOT"]
                            [weasel "0.7.0" :scope "test"]])

(require '[system.boot :refer [system run]]
         '[entrance-plus.systems :refer [dev-system]]
         '[clojure.edn :as edn]
         '[environ.core :refer [env]]
         '[environ.boot :refer [environ]])

(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-cljs-repl :refer :all]
         '[adzerk.boot-reload :refer :all])

(require '[airplanes.boot :refer [deploy]])

(def jar-name  (str "entrance-plus-" version "-standalone.jar"))

(task-options!
 aot {:namespace   #{'entrance-plus.core}}
 jar {:main        'entrance-plus.core
      :file        jar-name}
 pom {:project project
      :version version
      :license  {"Eclipse Public License"
                 "http://www.eclipse.org/legal/epl-v10.html"}}
 uber {:exclude #{#"(?i)^META-INF/[^/]*\.(MF|SF|RSA|DSA)$"
                  #"(?i)^META-INF\\[^/]*\.(MF|SF|RSA|DSA)$"
                  #"(?i)^META-INF/INDEX.LIST$"
                  #"(?i)^META-INF\\INDEX.LIST$"}})

(deftask dev
     "run a restartable system"
  []
  (comp
   (environ :env {:http-port "7000"})
   (watch :verbose true)
   (system :sys  #'dev-system
           :auto true
           :files ["routes.clj" "systems.clj"])
   (repl :server true
         :host "127.0.0.1")
   (reload :asset-path "public")
   (cljs-repl)
   (cljs :source-map true :optimizations :none)))

(deftask build
  "Build the project locally as a JAR."
  []
  (comp (aot) (pom) (uber) (jar) (sift :include #{#"entrance-plus-0.1.0-SNAPSHOT-standalone.jar"}) (target)))

(deftask d
  []
  (comp (aot) (pom) (uber) (jar) (deploy :name (str 'project)
                                         :dir "void-konserve"
                                         :ip "139.162.31.74")))


(deftask run-project
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (require '[kongauth.core :as app])
  (apply (resolve 'app/-main) args))

(require '[adzerk.boot-test :refer [test]])
