(ns bundle-tracker.core
  (:require [bundle-tracker.bundle :as bundle]
            [bundle-tracker.launch-services :as ls]
            [bundle-tracker.markdown :as markdown])
  (:gen-class))

(defn -main [& args]
  (let [opts (into #{} args)
        save? (opts ":save")
        dump (ls/ls-dump)
        types (bundle/ls-dump->bundle-types dump)
        table (markdown/bundle-types->table types)
        result (str "# Known bundle types #\n\n" table "\n")]
    (if save?
        (spit "KNOWN_TYPES.md" result)
        (println result))
    (System/exit 0)))
