(defproject silent-auction "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/java.jdbc "0.2.2"]
                 [clj-aws-s3 "0.3.2"]
                 [clj-imajine "0.1.9"]
                 [com.cemerick/friend "0.0.9"]
                 [compojure "1.1.1"]
                 [environ "0.3.0"]
                 [hiccup-bootstrap "0.1.0"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [ragtime/ragtime.core "0.2.0"]
                 [ring/ring-core "1.1.1"]
                 [ring/ring-json "0.1.0"]
                 [valip "0.2.0"]]
  :plugins [[lein-ring "0.7.4"]]
  :ring {:handler silent-auction.handler/app})
