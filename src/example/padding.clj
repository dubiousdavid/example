(ns example.padding)

(def ^{:dynamic true} *padding* 0)

(defmacro with-inc-padding
  "Increase padding by n in the scope of body."
  [n & body]
  `(binding [*padding* (+ *padding* ~n)]
     ~@body))

(defmacro with-padding
  "Set padding to n in the scope of body."
  [n & body]
  `(binding [*padding* ~n]
     ~@body))

(defn repeat-str [n ^String s]
  (apply str (repeat n s)))

(defn pad-left
  [s]
  (str (repeat-str *padding* " ") s))
