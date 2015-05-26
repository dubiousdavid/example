(ns example.util)

(defn sum [xs]
  (reduce + 0 xs))

(defn mapply [f & args]
  (apply f (apply concat (butlast args) (last args))))

(defn nl
  [s]
  (str s \newline))

(defn confirm
  [cnt]
  (println (str cnt " test(s) will be generated. Are you sure? (y/N)"))
  (let [answer (read)]
    (= answer 'y)))

(defn init-ns-examples [es e]
  (if es (conj es e) [e]))
