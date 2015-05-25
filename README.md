# example

An alternative to adding example fn calls in comment blocks. Simply wrap an example fn call in a call to the example macro and run `shex` in the REPL. See [example-prod](https://github.com/dubiousdavid/example-prod) for notes on using in production.

## Installation

Add the following to your repl profile in ~/.lein/profiles.clj.

```clojure
[com.2tothe8th "0.5.0+repl"]
```

## API Documentation

http://dubiousdavid.github.io/example

## How to use

In your source file:

```clojure
(ns my-ns
  (:use example.core))

(defn append [s1 s2] (str s1 s2))

(ex
  (append "Hello " "World")
  (append "Age: " 36))
```

More advanced usage:

```clojure
(ex "Math"
  (+ 5 5)
  (* 5 5))

(describe "Maps"
  (ex "assoc"
    (assoc {:a 1} :b 2)
    (assoc nil :a 1))
  (ex "dissoc"
    (dissoc {:a 1 :b 2} :a)
    (dissoc {:a 1 :b 2} :a :b)))
```

In the REPL:

```clojure
(use 'my-ns 'example.core)
(shex)
; or
(shex 'my-ns)
; or
(shex# my*)
; my-ns
;------
;(append "Hello " "World") => "Hello World"
```

You can also do the following:

```clojure
(unex 'my-ns)
(rex 'my-ns)
(cex)
```

When you’re ready to create tests (e.g., Midje), run one of the following:

```clojure
(use 'example.tests.midje)

(gen-facts 'my-ns)
(gen-facts-file 'my-ns
(gen-unit 'my-ns)
(gen-unit-file 'my-ns)
```

You can extend the Printable protocol, so that types example doesn't know about will be printed properly.

```clojure
(import 'org.joda.time.LocalDate)

(extend-protocol example.protocols/Printable
  LocalDate
  (printd [this] (list 'LocalDate. (.toString this))))
```
