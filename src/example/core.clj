(ns example.core
  "An alternative to adding example fn calls in comment blocks.
  Simply wrap an example fn call in a call to the example or
  examples macro and run show-examples in the REPL. Can
  also be used to generate unit tests."
  (:use [stch.glob :only [match-glob]]
        [example.data :only [all-examples]]
        [example.util :only [sum init-ns-examples]]
        [example padding wrapping]
        [example.generate :only [gen-ex-str]])
  (:require [clojure.string :as string]
            [example.colors :as color]
            [example.protocols :as proto :refer [printt counte printd printe]])
  (:import [clojure.lang Keyword Symbol ISeq IPersistentSet
            IPersistentVector APersistentMap Sequential IRecord]
           [example.types Example DescribeBlock ThrownException]))

(def ^{:dynamic true :private true} *outer-container* true)

(extend-protocol proto/Countable
  APersistentMap
  (counte [this] (sum (map counte (vals this))))
  Sequential
  (counte [this] (sum (map counte this))))

(extend-type Example
  proto/Printable
  (printe [this] (println (pad-left (gen-ex-str this))))

  proto/Countable
  (counte [this] 1))

(extend-type DescribeBlock
  proto/Printable
  (printe [this]
    (println (color/cyan (pad-left (.description this))))
    (doseq [f (.body this)]
      (with-inc-padding 2
        (printe f))))

  proto/Countable
  (counte [this] (sum (map counte (.body this)))))

(defmacro with-inner-container
  [& body]
  `(binding [*outer-container* false]
     ~@body))

(defmacro describe
  [description & body]
  `(add-example! (with-inner-container
                   (DescribeBlock. ~description (flatten (list ~@body))))))

(defn get-examples
  "Get all examples or all examples in a particular
  namespace. Useful for debugging."
  ([]
     @all-examples)
  ([ns-sym]
     (get @all-examples ns-sym)))

(defn drop-last!
  "Drop the last n examples in the current ns. Optionally,
  pass a namespace symbol."
  ([] (drop-last! 1))
  ([n]
     (drop-last! n (ns-name *ns*)))
  ([n ns-sym]
     (swap! all-examples update-in [ns-sym] #(vec (drop-last n %)))))

(defn add-example!
  "Add the example e if it does not exist for the current
  namespace. Returns nil."
  [e]
  (when *outer-container*
    (let [curr-ns (ns-name *ns*)
          ns-examples (@all-examples curr-ns)]
      (when-not (contains? (set ns-examples) e)
        (swap! all-examples update-in [curr-ns] init-ns-examples e))))
  e)

(defn- example*
  [form]
  `(Example. '~form (fn [] ~form)))

(defmacro example
  "Pass a form unquoted. Form will not be called
  unless show-examples is called."
  [form]
  `(do
     (add-example! ~(example* form))))

(defmacro ex
  "Create multiple examples at one time with optional
  description string."
  [& forms]
  (if (string? (first forms))
    (let [es (map example* (rest forms))]
      `(do
         (add-example! (DescribeBlock. ~(first forms) (list ~@es)))))
    (let [es (map (fn [f] `(example ~f)) forms)]
      `(list ~@es))))

(defmacro with-threshold
  "Print output on separate line when overall length
  is greater than or equal to x characters."
  [x & body]
  `(binding [*wrap-threshold* ~x]
     ~@body))

(defmacro wrap
  "Always print code and output on separate lines."
  [& body]
  `(with-threshold 0 ~@body))

(defmacro no-wrap
  "Always print code and output on one line."
  [& body]
  `(with-threshold 1000 ~@body))

(defn- print-ns-examples [ns-sym es]
  (println (color/cyan (list 'ns ns-sym)))
  (doseq [e es] (printe e))
  (println))

(defn shex
  "Show all the examples for all namespaces, or
  optionally pass a single namespace and one or more
  descriptions."
  ([]
     (when (-> @all-examples vals flatten seq)
       (println)
       (doseq [[ns-sym es] @all-examples]
         (print-ns-examples ns-sym es))))
  ([ns-sym]
     (let [ns-es (@all-examples ns-sym)]
       (when (seq ns-es)
         (println)
         (print-ns-examples ns-sym ns-es))))
  ([ns-sym & descs]
     (let [ns-es (@all-examples ns-sym)
           descs (set descs)
           blocks (filter (fn [e]
                            (when (and (instance? DescribeBlock e)
                                       (descs (.-description e)))
                              e))
                          ns-es)]
       (when (seq blocks)
         (println)
         (print-ns-examples ns-sym blocks)))))

(defn shex*
  "Given a glob pattern, show all examples for matching
  namespaces."
  [pattern]
  (let [matches (for [ns-sym (keys @all-examples)
                      :when (match-glob pattern (str ns-sym))]
                  ns-sym)]
    (when (seq matches)
      (println)
      (doseq [ns-sym matches]
        (print-ns-examples ns-sym (@all-examples ns-sym))))))

(defmacro shex#
  "Given a glob pattern, show all examples for matching
  namespaces.

  Example:
  (shex# my.*)"
  [pattern]
  `(shex* ~(name pattern)))

(defn cex
  "Clear all examples for all namespaces."
  []
  (reset! all-examples {})
  nil)

(defn unex
  "Unload all examples for the given namespace."
  [ns-sym]
  (swap! all-examples dissoc ns-sym)
  nil)

(defn rex
  "Reload all examples for the given namespace.
  Optionally pass one or more descriptions."
  ([ns-sym]
     (unex ns-sym)
     (require :reload ns-sym)
     (shex ns-sym))
  ([ns-sym & descs]
     (unex ns-sym)
     (require :reload ns-sym)
     (apply shex ns-sym descs)))

(extend-protocol proto/Printable
  nil
  (printd [this] this)
  Boolean
  (printd [this] this)
  String
  (printd [this] this)
  Number
  (printd [this] this)
  Keyword
  (printd [this] this)
  Symbol
  (printd [this] (symbol (str \' this)))
  ISeq
  (printe [this] (doseq [e this] (printe e)))
  (printt [this] (string/join \newline (map printt this)))
  (printd [this] (cons 'list (map printd this)))
  IRecord
  (printd [this]
    (list (symbol (str "map->" (.getSimpleName (class this))))
          (into {} (map (fn [[k v]] [k (printd v)]) this))))
  APersistentMap
  (printd [this] (->> (map (fn [[k v]] [(printd k) (printd v)]) this)
                      (into {})))
  IPersistentVector
  (printd [this] (vec (map printd this)))
  IPersistentSet
  (printd [this] (set (map printd this)))
  java.util.Date
  (printd [this] this)
  java.util.UUID
  (printd [this] this)
  ThrownException
  (printd [this] (let [e (.e this)]
                   (list 'throws (class e) (.getMessage e))))
  Throwable
  (printd [this] (list 'new (class this) (.getMessage this)))
  Class
  (printd [this] (symbol (.getName this)))
  Object
  (printd [this] (str "FIX-ME: " (pr-str this))))
