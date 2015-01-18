(ns bundle-tracker.markdown
  (:require [clojure.string :as string]))

(defn join-extensions [[description extensions]]
  (let [with-backticks (map #(str "`" % "`") extensions)]
    [description (string/join ", " with-backticks)]))

(defn longest-string [items]
  (reduce #(max %1 (count %2)) 0 items))

(defn fill-string [length c]
  (apply str (repeat length c)))

(defn pad-string [length s]
  (let [pad-length (- length (count s))
        padding (fill-string pad-length " ")]
    (str s padding)))

(defn table [headers body]
  (let [separators (map #(fill-string (count %) "-") headers)
        sections (concat [headers] [separators] body)
        rows (map #(string/join " | " %) sections)]
    (string/join "\n" rows)))

(defn bundle-types->table
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
