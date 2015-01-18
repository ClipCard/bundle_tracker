(ns bundle-tracker.launch-services-test
  (:use midje.sweet)
  (:require [bundle-tracker.launch-services :refer :all]))

(def output (slurp "fixtures/lsregister_dump.txt"))

(facts "about parsing LaunchServices state"
  (let [ls-data (read-ls-types output)
        first-item (first ls-data)
        third-item (nth ls-data 2)]
    (fact "it only reads `type` items"
      (every? #(= (:type %) "type") ls-data) => true)

    (fact "it reads the `uti` field"
      (first-item "uti") => "com.apple.itunes-producer.itmsp")

    (fact "it reads the `description` field"
      (first-item "description") => "iTunes Package")

    (fact "it reads the `bindings` field as a set"
      (first-item "bindings") => #{"com.fake.whatever" ".whatever"})

    (fact "it reads the `conforms to` field as a set"
      (first-item "conforms to") => #{"com.apple.package" "public.composite-content"})

    (fact "it reads the `tags` field as a set"
      (third-item "tags") => #{".adiumplugin" "'AdIM'"})))
