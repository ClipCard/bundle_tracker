(ns bundle-tracker.bundle-test
  (:use midje.sweet)
  (:require [bundle-tracker.bundle :refer :all]))

(def output (slurp "fixtures/lsregister_dump.txt"))

(facts "about mapping bundle types"
  (let [bundle-types (ls-dump->bundle-types output)]
    (fact "it finds extensions for native bundle and package types"
      (bundle-types "Adium chat log") => #{".adiumlog" ".chatlog"})

    (fact "it finds extensions for UTIs that conform to bundle and package types"
      (bundle-types "Adium plug-in") => #{".adiumplugin"})

    (fact "it merges extension sets for the same description"
      (bundle-types "Adium Libpurple plug-in")
      => #{".adiumlibpurpleplugin" ".fake-libpurpleplugin"})))
