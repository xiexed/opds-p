;(defproject my-stuff "0.1.0-SNAPSHOT"
;  :description "FIXME: write description"
;  :url "http://example.com/FIXME"
;  :license {:name "Eclipse Public License"
;            :url "http://www.eclipse.org/legal/epl-v10.html"}
;  :dependencies [[org.clojure/clojure "1.8.0"]]
;  :main ^:skip-aot my-stuff.core
;  :target-path "target/%s"
;  :profiles {:uberjar {:aot :all}})

(defproject hello-world "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler hello-world.core/handler}
  )
