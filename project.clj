(defproject silent-auction "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.0"]
                 [hiccup-bootstrap "0.1.0"]]
  :plugins [[lein-ring "0.7.1"]]
  :ring {:handler silent-auction.handler/app})
