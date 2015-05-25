(ns example.util
  (:use example.padding
        [clojure.pprint :only [pprint]])
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [example.types :as types]))

(defn sum [xs]
  (reduce + 0 xs))

(defn mapply [f & args]
  (apply f (apply concat (butlast args) (last args))))

(defn nl
  [s]
  (str s \newline))

(defn split-ns
  [ns]
  (string/split (name ns) #"\."))

(defn append-last
  [v s]
  (update-in v [(- (count v) 1)] str s))

(defn dash->underscore
  [s]
  (string/replace s "-" "_"))

(defn confirm
  [cnt]
  (println (str cnt " test(s) will be generated. Are you sure? (y/N)"))
  (let [answer (read)]
    (= answer 'y)))

(defn init-ns-examples [es e]
  (if es (conj es e) [e]))

(defn mk-ns-ref
  [type refs]
  (when (seq refs) (list* type refs)))

(defn parse-ns
  [ref]
  (if (sequential? ref) (first ref) ref))

(defn conj-ns-ref
  "Conj a namespace ref, if it does not already exist."
  [refs ref]
  (let [namespaces (set (map parse-ns refs))]
    (if-not (namespaces (parse-ns ref))
      (conj refs ref)
      refs)))
