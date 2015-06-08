# example

Write your test code inline with your functions, test the output of your functions in the REPL as you code, and generate unit tests when you're satisfied with the behavior of your functions.

## Installation

Add the following to the `:dependencies` vector in the `repl` profile in `~/.lein/profiles.clj`.

```clojure
[com.2tothe8th/example "0.5.0+repl"]
```

See [example-prod](https://github.com/dubiousdavid/example-prod) for notes on how to turn calls to `ex` and `describe` to comment blocks for use in production.

## API Documentation

http://dubiousdavid.github.io/example

## How to use

The starting point for using example is your source code. The idea is that as you are writing a function, you want to test various inputs to that function and check that the output is what you would expect. Let's take a look at an example.

```clojure
(ns my-ns
  (:use example.core))

(defn sum-scores [scores]
  (reduce + 0 (map :score scores)))

(ex "sum-scores"
  (sum-scores [{:score 10} {:score 8} {:score 9}])
  (sum-scores []))
```

In the file above I created a function `sum-scores` that sums a vector of maps, where each map contains a key :score with a numeric score. Below that function I call `ex`, short for example, passing an optional description (in this case the name of the function), and one or more example calls to that function.


In a REPL I load my namespace and `example.core` and call the `shex` function, short for show examples. This prints the namespace of the file, followed by all the example calls and their output.

```clojure
(use 'my-ns 'example.core)

(shex)
```

![output-1](https://raw.githubusercontent.com/dubiousdavid/example/master/images/output-1.png)

Let's assume now that we need to make another change to our function. After doing that, we can reload our namespace and examples for that namespace by calling `(rex 'my-ns)`. In addition we can:

```clojure
;; Unload namespace examples
(unex 'my-ns)
;; Clear examples from all namespaces
(cex)
```

When we think we're ready to create unit tests (e.g., Midje), we can call [gen-facts](http://dubiousdavid.github.io/example/example.tests.midje.html#var-gen-facts) to preview our test file output.

```clojure
(use 'example.tests.midje)

(gen-facts 'my-ns)
```

![output-2](https://raw.githubusercontent.com/dubiousdavid/example/master/images/output-2.png)

If we're satisfied with the the tests that will be generated we can call [gen-facts-file](http://dubiousdavid.github.io/example/example.tests.midje.html#var-gen-facts-file) to generate the actual file.

```clojure
(gen-facts-file 'my-ns)
```

![output-3](https://raw.githubusercontent.com/dubiousdavid/example/master/images/output-3.png)

Both of the above functions take lot's of different options to custom tailor the actual test file that is generated. See the API docs for more information.

### Describe

I we need to group more than one set of examples together we can use [describe](http://dubiousdavid.github.io/example/example.core.html#var-describe) to do that.

```clojure
(describe "Maps"
  (ex "assoc"
    (assoc {:a 1} :b 2)
    (assoc nil :a 1))
  (ex "dissoc"
    (dissoc {:a 1 :b 2} :a)
    (dissoc {:a 1 :b 2} :a :b)))
```

### Production usage

Now you might be asking yourself "What about production code? Doesn't this hurt performance."  The answer to that problem is to add `[com.2tothe8th/example "0.4.0"]` to your project's primary :dependencies vector. This turns all calls to `ex` and `describe` to comment blocks (i.e, they produce no forms at macro expansion time). This ensures that there is no penalty for having this code inline with your source code.

### Printable protocol

You can extend the [Printable](http://dubiousdavid.github.io/example/example.protocols.html#var-Printable) protocol, so that types example doesn't know about will be printed properly. For example, to make example aware of how to generate source code for a LocalDate we would do the following:

```clojure
(import 'org.joda.time.LocalDate)

(extend-protocol example.protocols/Printable
  LocalDate
  (printd [this] (list 'LocalDate. (.toString this))))
```
