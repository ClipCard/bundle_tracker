(ns bundle-tracker.bundle
  (:require [bundle-tracker.launch-services :as ls]
            [bundle-tracker.overrides :as overrides]))

(def ^:dynamic *known-types*
  (read-string (slurp "known_types.edn")))

(def ^:dynamic *bundle-utis*
  #{"com.apple.bundle"
    "com.apple.package"})

(defn conforms? [item]
  (some *bundle-utis* (item "conforms to")))

(defn with-conforming-uti [acc item]
  (if (conforms? item)
      (conj acc (item "uti"))
      acc))

(defn conforming-utis [items]
  (reduce with-conforming-uti #{} items))

(defn item-description [item]
  (or (item "description")
      (item "uti")))

(defn ext? [x]
  (and (string? x) (re-matches #"^\.\S+$" x)))

(defn item-extensions [item]
  (let [{bindings "bindings" tags "tags"} item]
    (into #{} (filter ext? (concat bindings tags)))))

(defn merge-set [acc k v]
  (let [current (acc k)
        merged (if current (into #{} (concat current v)) v)]
    (assoc acc k merged)))

(defn merge-sets [a b]
  (reduce #(apply merge-set %1 %2) a b))

(defn with-extensions [acc item]
  (let [k (item-description item)
        extensions (item-extensions item)]
    (if (empty? extensions)
        acc
        (merge-set acc k extensions))))

(defn with-conforming-type [acc item]
  (if (conforms? item)
      (with-extensions acc item)
      acc))

(defn override-descriptions [acc [description extensions]]
  (if-let [override (overrides/*description* description)]
    (-> acc
        (assoc override extensions)
        (dissoc description))
    acc))

(defn ls-dump->bundle-types
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
