(ns bundle-tracker.markdown
  (:require [clojure.string :as string]))

(defn join-extensions
  ^{:doc "Returns a key-value pair where `extensions` has been joined and
         formatted as Markdown code."
    :tag clojure.lang.APersistentVector}
  [[description extensions]]
  (let [with-backticks (map #(str "`" % "`") extensions)]
    [description (string/join ", " with-backticks)]))

(defn longest-string
  ^{:doc "Determines the longest string in a sequence."
    :tag Number}
  [items]
  (reduce #(max %1 (count %2)) 0 items))

(defn fill-string
  ^{:doc "Returns a string filled with character `c` up to `length`"
    :tag java.lang.String}
  [length c]
  (apply str (repeat length c)))

(defn pad-string
  ^{:doc "Returns a string padded with whitespace to `length`
         (padding is added on the right)."
    :tag java.lang.String}
  [length s]
  (let [pad-length (- length (count s))
        padding (fill-string pad-length " ")]
    (str s padding)))

(defn table
  ^{:doc "Returns a Markdown (GFM) formatted table."
    :tag java.lang.String}
  [headers body]
  (let [separators (map #(fill-string (count %) "-") headers)
        sections (concat [headers] [separators] body)
        rows (map #(string/join " | " %) sections)]
    (string/join "\n" rows)))

(defn bundle-types->table
  ^{:doc "Returns a Markdown (GFM) formatted table of `types` derived
         from a LaunchServices dump."
    :tag java.lang.String}
  [types]
  (let [with-joined-extensions (map join-extensions types)
        descriptions (map first with-joined-extensions)
        longest-description (inc (longest-string descriptions))
        extensions (map second with-joined-extensions)
        longest-extension (inc (longest-string extensions))
        headers [(pad-string longest-description "Description")
                 (pad-string longest-extension "Extensions")]
        body (map (fn [[description extensions]]
                    [(pad-string longest-description description)
                     (pad-string longest-extension extensions)])
                  with-joined-extensions)]
    (table headers body)))
