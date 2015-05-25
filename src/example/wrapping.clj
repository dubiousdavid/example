(ns example.wrapping)

(def ^{:dynamic true} *wrap-threshold* 80)

(defn should-wrap?
  [cnt]
  (> cnt *wrap-threshold*))

(defn wrap-or-space
  "Output a newline if character count exceeds wrap threshold,
  otherwise a space."
  [cnt]
  (if (should-wrap? cnt) \newline \space))
