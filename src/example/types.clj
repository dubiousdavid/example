(ns example.types)

(deftype Example [code f]
  Object
  (equals [this that]
    (if-not (instance? Example that)
      false
      (= code (.code that))))
  (hashCode [this] (.hashCode code)))

(deftype DescribeBlock [description body]
  Object
  (equals [this that]
    (if-not (instance? DescribeBlock that)
      false
      (= body (.body that))))
  (hashCode [this] (.hashCode body)))

(deftype ThrownException [e]
  Object
  (toString [this] (.toString e)))
