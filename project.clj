(defproject bundle_tracker "0.3"
  :description "Tracks Mac OS X bundle and package folder types"
  :license {}
  :main bundle-tracker.core
  :plugins [[lein-midje "3.0.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.6.3"]
                 [cheshire "5.4.0"]
                 [doric "0.9.0"]])
