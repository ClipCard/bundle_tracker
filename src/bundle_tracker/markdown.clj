(ns bundle-tracker.markdown
  (:require [clojure.string :as string]
            [doric.core :as doric]))

(defn join-extensions
  ^{:doc "Join extensions set with commas, marking each extension with backticks"
    :tag java.lang.String}
  [extensions]
  (let [with-backticks (map #(str "`" % "`") extensions)]
    (string/join ", " with-backticks)))

(defn th
  ^{:doc "Generate table header"
    :tag java.lang.String}
  [& args]
  (let [s (apply doric/aligned-th args)]
    (str s " ")))

(defn td
  ^{:doc "Generate table cell"
    :tag java.lang.String}
  [& args]
  (let [s (apply doric/aligned-td args)]
    (str s " ")))

(defn join-row
  ^{:doc "Generate table row"
    :tag java.lang.String}
  [row]
  (string/join " | " row))

(defn render
  ^{:doc "Generate table string"
    :tag java.lang.String}
  [table]
  (let [header-row (first table)
        header (join-row header-row)
        separator-row (map #(apply str (repeat (count %) "-")) header-row)
        separator (join-row separator-row)]
    (concat [header separator]
            (map join-row (rest table)))))

(defn row
  ^{:doc "Returns table row map"
    :tag clojure.lang.APersistentMap}
  [[k v]]
  {"Description" k
   "Extensions" (join-extensions v)})

(defn bundle-types->table
  ^{:doc "Generate table from bundle types sequence"
    :tag java.lang.String}
  [types]
  (let [headers [{:name "Description" :align :left}
                 {:name "Extensions" :align :left}]
        rows (map row types)]
    (doric/table {:format 'bundle-tracker.markdown} headers rows)))
