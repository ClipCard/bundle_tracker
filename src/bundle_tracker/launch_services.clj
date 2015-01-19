(ns bundle-tracker.launch-services
  (:require [clojure.java.shell :as shell]
            [clojure.string :as string]))

;; Read output
(defn split-commas
  ^{:doc "Returns a set of values from a comma-separated string"
    :tag clojure.lang.APersistentSet}
  [value]
  (into #{} (string/split value #",\s+")))

(defn read-value
  ^{:doc "Returns a value from a LaunchServices dump"}
  [field value]
  (cond
    (#{"bindings"
       "conforms to"
       "tags"} field) (split-commas value)
    (empty? value) nil
    :else value))

(def ^{:doc "Matches a field in a LaunchServices dump"}
  field-pattern
  #"^\s*([^:]+)(?::\s*(.*))?$")

(defn read-field
  ^{:doc "Returns a vector of [`field` `value`] from a LaunchServices dump"
    :tag clojure.lang.APersistentVector}
  [row]
  (let [row-matches (re-find field-pattern row)
        field (nth row-matches 1)
        value (nth row-matches 2)]
    [field (read-value field value)]))

(defn read-fields
  ^{:doc "Returns a map of fields from a LaunchServices dump"
    :tag clojure.lang.APersistentMap}
  [item-fields]
  (let [rows (string/split item-fields #"\n")]
    (into {} (map read-field rows))))

(def ^{:doc "Matches a header in a LaunchServices dump. (Typically discarded.)"}
  item-header-pattern
  #"^Container mount state: .*?\n")

(def ^{:doc "Matches a Property List value in a LaunchServices dump. (Typically discarded)"}
  plist-pattern
  #"\n<\?xml[\s\S]*?\n</plist>")

(defn sanitize-item
  ^{:doc "Strips headers and Property Lists from a LaunchServices dump item."
    :tag java.lang.String}
  [item]
  (let [without-header (string/replace item item-header-pattern "")]
    (string/replace without-header plist-pattern "")))

(def ^{:doc "Matches the type of a LaunchServices dump item"}
  item-type-pattern
  #"^(\s*)(\S+)")

(defn read-type-item
  ^{:doc "Reads a `type` item from a LaunchServices dump. Returns a map of fields."
    :tag clojure.lang.APersistentMap}
  [item]
  (let [sanitized (sanitize-item item)
        item-type (nth (re-find item-type-pattern sanitized) 2)
        item-fields (string/replace sanitized item-type-pattern "$1")]
    (when (= item-type "type")
      (merge {:type item-type} (read-fields item-fields)))))

(def ^{:doc "Matches separators between LaunchServices items"}
  item-separator-pattern
  #"(?m)^\s+-+$\n*")

(defn read-type-items
  ^{:doc "Returns a sequence of `type` items from a LaunchServices dump."
    :tag clojure.lang.ASeq}
  [group]
  (let [items (string/split group item-separator-pattern)]
    (filter identity (map read-type-item items))))

(def ^{:doc "Matches a separator between groups of items from a LaunchServices dump"}
  group-separator-pattern
  #"(?m)^-+$\n*")

(defn read-ls-types
  ^{:doc "Retruns a vector of types from a LaunchServices dump"
    :tag clojure.lang.APersistentVector}
  [dump]
  (let [groups (string/split dump group-separator-pattern)]
    (into [] (mapcat read-type-items (rest groups)))))

;; Shell
(def ^{:doc "The command to access the LaunchServices database front-end"}
  lsregister
  (str "/System/Library/Frameworks/CoreServices.framework"
       "/Versions/A/Frameworks/LaunchServices.framework"
       "/Versions/A/Support/lsregister"))

(defn ls-dump
  ^{:doc "Returns the current state of the LaunchServices database as a dump."
    :tag java.lang.String}
  []
  (:out (shell/sh lsregister "-dump")))
