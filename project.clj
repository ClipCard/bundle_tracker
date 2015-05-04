(defproject bundle_tracker "0.9.1"
  :url "https://github.com/ClipCard/bundle_tracker"
  :description "Tracks Mac OS X bundle and package folder types"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"
            :key "mit"
            :year 2015
            :distribution :repo}
  :main bundle-tracker.core
  :plugins [[lein-midje "3.0.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.6.3"]
                 [cheshire "5.4.0"]
                 [doric "0.9.0"]])
