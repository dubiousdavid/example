(ns example.generate
  (:import java.io.StringWriter example.types.ThrownException))

(defn mk-ns-dec
  [library-ns test-ns ns {:keys [import require use] :or {use []}}]
  (let [i (mk-ns-ref :import import)
        r (mk-ns-ref :require require)
        use-refs (-> use
                     (conj-ns-ref library-ns)
                     (conj-ns-ref ns))
        u (mk-ns-ref :use use-refs)
        refs (remove nil? (list i r u))]
    (str "(ns " test-ns \newline
         (with-padding 2
           (->> (map pad-left refs)
                (string/join \newline))) ")")))

(defn mk-test-ns
  [ns test-dir file-append]
  (let [dir-parts (rest (string/split test-dir #"/"))
        ns-parts (vec (concat dir-parts (split-ns ns)))]
    (->> (append-last ns-parts file-append)
         (string/join ".")
         symbol)))

(defn mk-out-file
  [ns test-dir file-append]
  (let [ns-parts (split-ns ns)]
    (->> (append-last ns-parts (str file-append ".clj"))
         (cons test-dir)
         (map dash->underscore)
         (apply io/file))))

(defn pp-str
  "Pretty print object. Returns a string."
  [obj]
  (let [writer (StringWriter.)]
    (pprint obj writer)
    (let [s (string/trimr (.toString writer))
          parts (string/split s #"\n")]
      (if (> (count parts) 1)
        (->> (map pad-left parts)
             (string/join \newline))
        (pad-left s)))))

(defn call-fn
  "Call the fn, catching fns and wrapping them in a
  ThrownException record."
  [f]
  (try (f) (catch Throwable e (ThrownException. e))))

(defn gen-ex-str
  "Generate example string."
  [e]
  (let [code-str (pr-str (.code e))
        output (call-fn (.f e))
        cnt (+ (count code-str) (count (pr-str output)) 4)
        output-str (if (should-wrap? cnt)
                     (with-inc-padding 2 (pp-str output))
                     (with-padding 0 (pp-str output)))]
    (str (color/magenta code-str) " =>" (wrap-or-space cnt) output-str)))
