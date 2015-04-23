(ns bundle-tracker.bundle-test
  (:use midje.sweet)
  (:require [bundle-tracker.bundle :refer :all]
            [bundle-tracker.overrides :as overrides]))

(def output (slurp "fixtures/lsregister_dump.txt"))

(facts "about mapping bundle types"
  (let [bundle-types (ls-dump->bundle-types output)]
    (fact "it finds extensions for native bundle and package types"
      (bundle-types "Adium chat log") => #{".adiumlog" ".chatlog"})

    (fact "it finds extensions for UTIs that conform to bundle and package types"
      (bundle-types "Adium plug-in") => #{".adiumplugin"})

    (fact "it merges extension sets for the same description"
      (bundle-types "Adium Libpurple plug-in")
      => #{".adiumlibpurpleplugin" ".fake-libpurpleplugin"})

    (fact "it merges detected extensions with known types"
      (binding [*known-types* {"Adium chat log" #{".fake-log"}}]
        (let [bundle-types- (ls-dump->bundle-types output)]
          (bundle-types- "Adium chat log") => #{".fake-log" ".adiumlog" ".chatlog"})))

    (fact "it overrides select descriptions"
      (binding [overrides/*description* {"Adium chat log" "Overridden"}]
        (let [bundle-types- (ls-dump->bundle-types output)]
          (bundle-types- "Overridden") => #{".adiumlog" ".chatlog"})))

    (fact "it looks up a known type by filename"
      (binding [*known-types* {"Adium chat log" #{".adiumlog" ".chatlog"}}]
        (filename->known-type "foo.adiumlog") => "Adium chat log"
        (filename->known-type "/this/presumably/supports/paths.adiumlog")
        => "Adium chat log"
        (filename->known-type "/there/is/no.match") => nil))

    (fact "it determines a path's containing bundle type"
      (binding [*known-types* {"Application" #{".app"}}]
        (path->containing-type "/Applications/Foo.app/Contents/Info.plist")
        => "Application"))))
