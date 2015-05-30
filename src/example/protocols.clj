(ns example.protocols)

(defprotocol Printable
  (printe [this]
    "Takes an example and prints the example and it’s output.")
  (printd [this]
    "Takes an object and returns it in a form that when printed,
    can be run as code."))

(defprotocol Countable
  (counte [this]
    "Returns the total example count."))

(defprotocol Testable
  (printt [this]
    "Takes an example and returns a unit test."))
