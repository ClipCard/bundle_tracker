(ns bundle-tracker.core
  (:require [clojure.pprint :refer [pprint]]
            [cheshire.core :as json]
            [bundle-tracker.bundle :as bundle]
            [bundle-tracker.launch-services :as ls]
            [bundle-tracker.markdown :as markdown])
  (:gen-class))

(defn pretty-print-edn
  ^{:doc "Returns pretty-printed `x` in EDN notation"
    :tag java.lang.String}
  [x]
  (with-out-str (pprint x)))

(defn pretty-print-json
  ^{:doc "Returns pretty-printed `x` in JSON notation"
    :tag java.lang.String}
  [x]
  (json/generate-string x {:pretty true}))

(defn save!
  ^{:doc "Saves `types` as EDN, JSON and Markdown"}
  [types]
  (println "Saving known_types.edn")
  (spit "known_types.edn" (pretty-print-edn types))

  (println "Saving known_types.json")
  (spit "known_types.json" (pretty-print-json types))

  (println "Saving KNOWN_TYPES.md")
  (let [table (markdown/bundle-types->table types)
        md (str "# Known bundle types #\n\n" table "\n")]
    (spit "KNOWN_TYPES.md" md)))

(defn -main
  ^{:doc "Track bundles on local Mac. Either outputs pretty-printed
         types on local system (plus known types) or stores detected
         types as additional known types."}
  [& args]
  (let [opts (into #{} args)
        save? (opts ":save")
        dump (ls/ls-dump)
        types (bundle/ls-dump->bundle-types dump)]
    (if save?
        (save! types)
        (pprint types))
    (System/exit 0)))
