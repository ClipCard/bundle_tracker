(ns bundle-tracker.core
  (:require [clojure.pprint :refer [pprint]]
            [cheshire.core :as json]
            [bundle-tracker.bundle :as bundle]
            [bundle-tracker.launch-services :as ls]
            [bundle-tracker.markdown :as markdown])
  (:gen-class))

(defn pretty-print-edn [x]
  (with-out-str (pprint x)))

(defn pretty-print-json [x]
  (json/generate-string x {:pretty true}))

(defn save [types]
  (println "Saving known_types.edn")
  (spit "known_types.edn" (pretty-print-edn types))

  (println "Saving known_types.json")
  (spit "known_types.json" (pretty-print-json types))

  (println "Saving KNOWN_TYPES.md")
  (let [table (markdown/bundle-types->table types)
        md (str "# Known bundle types #\n\n" table "\n")]
    (spit "KNOWN_TYPES.md" md)))

(defn -main [& args]
  (let [opts (into #{} args)
        save? (opts ":save")
        dump (ls/ls-dump)
        types (bundle/ls-dump->bundle-types dump)]
    (if save?
        (save types)
        (pprint types))
    (System/exit 0)))
