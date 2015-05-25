(ns example.colors)

(def ^{:dynamic true} *color-on* true)

(defmacro with-color-off
  "Turn color off when printing examples/facts."
  [& body]
  `(binding [*color-on* false]
     ~@body))

(defn colorize [ansi s]
  (str "\u001B[" ansi "m" s "\u001B[0m"))

(def bold (partial colorize 1))
(def red (partial colorize 31))
(def green (partial colorize 32))
(def yellow (partial colorize 33))
(def blue (partial colorize 34))
(def magenta (partial colorize 35))
(def cyan (partial colorize 36))
