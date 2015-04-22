(ns bundle-tracker.bundle
  (:require [bundle-tracker.launch-services :as ls]
            [bundle-tracker.overrides :as overrides]))

(def ^{:dynamic true
       :doc "Refers current state of known types. This state will be used
            to produce additive changes as users run the project."}
  *known-types*
  (read-string (slurp "known_types.edn")))

(def ^{:doc "Given a map of known type -> extension, returns a map of
            extension -> known type."
       :tag clojure.lang.APersistentMap}
  known-types-by-extension
  (memoize
    (fn [known-types]
      (reduce (fn [result [type extensions]]
        (into result (map #(vec [% type]) extensions))) (sorted-map) known-types))))

(defn extension->known-type
  ^{:doc "Given an extension, returns a known type definition (or nil)"
    :tag String}
  [extension]
  (let [known-types (known-types-by-extension *known-types*)]
    (known-types extension)))

(defn filename->known-type
  ^{:doc "Given a filename, returns a known type definition (or nil)"
    :tag String}
  [filename]
  (let [extension (re-find #"\.[^.]+$" filename)]
    (extension->known-type extension)))

(def ^{:dynamic true
       :doc "References bundle and package UTI types. Can be bound to
            conforming types to identify bundle extensions."}
  *bundle-utis*
  #{"com.apple.bundle"
    "com.apple.package"})

(defn conforms?
  ^{:doc "Determines if an item conforms to *bundle-utils*."
    :tag Boolean}
  [item]
  (some *bundle-utis* (item "conforms to")))

(defn with-conforming-uti
  ^{:doc "Includes an item's `uti` in `acc` if the item conforms to *bundle-utis*."
    :tag clojure.lang.APersistentSet}
  [acc item]
  (if (conforms? item)
      (conj acc (item "uti"))
      acc))

(defn conforming-utis
  ^{:doc "Returns all UTIs from `items` conforming to `*bundle-utis*`"
    :tag clojure.lang.APersistentSet}
  [items]
  (reduce with-conforming-uti #{} items))

(defn item-description
  ^{:doc "Returns an item's description, falling back to its UTI."
    :tag java.lang.String}
  [item]
  (or (item "description")
      (item "uti")))

(defn ext?
  ^{:doc "Determines if `x` looks like an extension string."
    :tag Boolean}
  [x]
  (and (string? x) (re-matches #"^\.\S+$" x)))

(defn item-extensions
  ^{:doc "Returns a set of `bindings` and `tags` for an item that look like extensions."
    :tag clojure.lang.APersistentSet}
  [item]
  (let [{bindings "bindings" tags "tags"} item]
    (into #{} (filter ext? (concat bindings tags)))))

(defn merge-set
  ^{:doc "Associates a set for `k` where the value is either the combined sets of the
         original value and `v`, or `v` where there was no original value."
    :tag clojure.lang.APersistentMap}
  [acc k v]
  (let [current (acc k)
        merged (if current (into #{} (concat current v)) v)]
    (assoc acc k merged)))

(defn merge-sets
  ^{:doc "Returns a map of sets where `a`'s values are conjoined with `b`, and
         `b`'s values are provided for keys not present in `a`."
    :tag clojure.lang.APersistentMap}
  [a b]
  (reduce #(apply merge-set %1 %2) a b))

(defn with-extensions
  ^{:doc "Returns a map of descriptions from `item` to a set of values that look
         like extension strings from `item`'s `bindings` and `tags`, adding to
         any existing extensions in `acc`."
    :tag clojure.lang.APersistentMap}
  [acc item]
  (let [k (item-description item)
        extensions (item-extensions item)]
    (if (empty? extensions)
        acc
        (merge-set acc k extensions))))

(defn with-conforming-type
  ^{:doc "Associates extensions from `item` if it conforms to `*bundle-utis*`."
    :tag clojure.lang.APersistentMap}
  [acc item]
  (if (conforms? item)
      (with-extensions acc item)
      acc))

(defn override-descriptions
  ^{:doc "Where override descriptions are provided, replaces existing mappings
         of description->extensions with overrides."
    :tag clojure.lang.APersistentMap}
  [acc [description extensions]]
  (if-let [override (overrides/*description* description)]
    (-> acc
        (assoc override extensions)
        (dissoc description))
    acc))

(defn ls-dump->bundle-types
  ^{:arglists '([dump])
    :doc "Produces a sequence of bundle types `[description extensions]` for
         a LaunchServices dump."
    :tag clojure.lang.ASeq}
  ([dump]
    (let [items (ls/read-ls-types dump)]
      (ls-dump->bundle-types *bundle-utis* items)))
  ([bundle-utis items]
    (binding [*bundle-utis* bundle-utis]
      (let [child-utis (conforming-utis items)
            child-types (when (seq child-utis)
                          (ls-dump->bundle-types child-utis items))
            conforming-types (reduce with-conforming-type *known-types* items)
            merged (merge-sets conforming-types child-types)
            overridden (reduce override-descriptions merged merged)]
        (into (sorted-map) overridden)))))
