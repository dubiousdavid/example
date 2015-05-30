(ns example.tests.midje
  (:use [example.generate :only [mk-ns-dec mk-test-ns mk-out-file call-fn pp-str]]
        [example.data :only [all-examples]]
        [example.util :only [mapply confirm nl]]
        [example padding wrapping])
  (:require [clojure.string :as string]
            [example.protocols :as proto :refer [printt counte printd]]
            [example.colors :as color :refer [with-color-off]]
            [clojure.java.io :as io]
            example.types)
  (:import [example.types Example DescribeBlock]))

(defn mk-testable-privates
  [ns private-fns]
  (list* 'testable-privates ns private-fns))

(defn gen-facts
  "Generate facts from the examples from the given namespace."
  [ns & {:keys [test-dir file-append import require use private-fns]
         :or {test-dir "test" file-append "-test"}
         :as kw-args}]
  (when-let [examples (get @all-examples ns)]
    (let [test-ns (mk-test-ns ns test-dir file-append)
          references (select-keys kw-args [:import :require :use])
          private-fns? (seq private-fns)
          midje-util-ref '[midje.util :only [testable-privates]]
          ns-dec (->> (if private-fns?
                        (update-in references [:use] conj midje-util-ref)
                        references)
                      (mk-ns-dec 'midje.sweet test-ns ns))]
      (prn)
      (println (color/cyan ns-dec))
      (when private-fns?
        (println (mk-testable-privates ns private-fns)))
      (prn)
      (doseq [e examples]
        (println (printt e)))
      (prn))))

(defn gen-unit
  "Same as gen-facts but sets test-dir to test/unit
  and does not append -test to file."
  [ns & {:as kw-args}]
  (->> (assoc kw-args :test-dir "test/unit" :file-append nil)
       (mapply gen-facts ns)))

(defn gen-facts-file
  "Generate a test file with examples converted into midje facts.
  File is created in the test directory by default with
  -test appended to the original file name. Both of these settings can
  be overridden."
  [ns & {:keys [test-dir file-append import require use private-fns]
         :or {test-dir "test" file-append "-test"}
         :as kw-args}]
  (if-let [examples (get @all-examples ns)]
    (if-not (confirm (counte examples))
      (println "Aborted")
      (let [out-file (mk-out-file ns test-dir file-append)
            test-ns (mk-test-ns ns test-dir file-append)
            references (select-keys kw-args [:import :require :use])
            private-fns? (seq private-fns)
            midje-util-ref '[midje.util :only [testable-privates]]
            references (if private-fns?
                         (update-in references [:use] conj midje-util-ref)
                         references)
            ns-dec (mk-ns-dec 'midje.sweet test-ns ns references)]
        (io/make-parents out-file)
        (with-open [w (io/writer out-file)]
          (with-color-off
            (.write w (nl ns-dec))
            (when private-fns?
              (.write w (nl (mk-testable-privates ns private-fns))))
            (.write w "\n")
            (doseq [e examples]
              (.write w (nl (printt e))))))
        (println (str "Wrote to file: " out-file))))
    (println (str "No examples found for ns: " ns))))

(defn gen-unit-file
  "Same as gen-facts-file but sets test-dir to test/unit
  and does not append -test to file."
  [ns & {:as kw-args}]
  (->> (assoc kw-args :test-dir "test/unit" :file-append nil)
       (mapply gen-facts-file ns)))

(defn gen-fact
  "Generate a fact from the given example."
  [e]
  (let [assertion (.code e)
        assertion-str (pr-str assertion)
        result (printd (call-fn (.f e)))
        cnt (+ (count assertion-str) (count (pr-str result)) 11)
        result-str (if (should-wrap? cnt)
                     (with-inc-padding 2 (pp-str result))
                     (with-padding 0 (pp-str result)))]
    (str "(fact " (color/magenta assertion-str) " =>" (wrap-or-space cnt) result-str ")")))

(extend-protocol proto/Testable
  DescribeBlock
  (printt [this]
    (let [contents (with-inc-padding 2
                     (->> (.body this)
                          (map printt)
                          (map pad-left)
                          (string/join \newline)))]
      (str "(facts " (nl (color/cyan (pr-str (.description this)))) contents ")")))

  Example
  (printt [this] (gen-fact this)))
