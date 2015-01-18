(ns bundle-tracker.launch-services
  (:require [clojure.java.shell :as shell]
            [clojure.string :as string]))

;; Read output
(defn split-commas [value]
  (into #{} (string/split value #",\s+")))

(defn read-value [field value]
  (cond
    (#{"bindings"
       "conforms to"
       "tags"} field) (split-commas value)
    (empty? value) nil
    :else value))

(def field-pattern
  #"^\s*([^:]+)(?::\s*(.*))?$")

(defn read-field [row]
  (let [row-matches (re-find field-pattern row)
        field (nth row-matches 1)
        value (nth row-matches 2)]
    [field (read-value field value)]))

(defn read-fields [item-fields]
  (let [rows (string/split item-fields #"\n")]
    (into {} (map read-field rows))))

(def item-header-pattern
  #"^Container mount state: .*?\n")

(def plist-pattern
  #"\n<\?xml[\s\S]*?\n</plist>")

(defn sanitize-item [item]
  (let [without-header (string/replace item item-header-pattern "")]
    (string/replace without-header plist-pattern "")))

(def item-type-pattern
  #"^(\s*)(\S+)")

(defn read-item [item]
  (let [sanitized (sanitize-item item)
        item-type (nth (re-find item-type-pattern sanitized) 2)
        item-fields (string/replace sanitized item-type-pattern "$1")]
    (when (= item-type "type")
      (merge {:type item-type} (read-fields item-fields)))))

(def item-separator-pattern
  #"(?m)^\s+-+$\n*")

(defn read-items [group]
  (let [items (string/split group item-separator-pattern)]
    (filter identity (map read-item items))))

(def group-separator-pattern
  #"(?m)^-+$\n*")

(defn read-ls-types [dump]
  (let [groups (string/split dump group-separator-pattern)]
    (into [] (mapcat read-items (rest groups)))))

;; Shell
(def lsregister
  (str "/System/Library/Frameworks/CoreServices.framework"
       "/Versions/A/Frameworks/LaunchServices.framework"
       "/Versions/A/Support/lsregister"))

(defn ls-dump []
  (:out (shell/sh lsregister "-dump")))
