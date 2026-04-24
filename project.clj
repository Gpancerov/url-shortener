(defproject url-shortener "0.1.0"
  :description "URL Shortener service"
  :url "https://example.com"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [compojure "1.6.2"]
                 [clj-http "3.12.3"]
                 [org.xerial/sqlite-jdbc "3.42.0.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [ring/ring-json "0.5.1"]]
  :main ^:skip-aot url-shortener.core
  :target-path "target/%s"
  :uberjar-name "url-shortener-0.1.0-standalone.jar"
  :profiles {:uberjar {:aot :all}})