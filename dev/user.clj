(ns user)

(ex (map inc (range 5)))
(ex (first '[a b c]))
(ex (keys {:a 1 'b 2 "c" 3}))
(ex (= true true))
(ex (= 1.0 nil))
(ex (assoc {:a 1} :b ["David" :Billy]))
(ex (conj #{:a} :b))
(ex (get {:a 1} :b))
(ex "Literals"
    #inst "2014-01-01"
    #uuid "381877aa-02e4-4bd0-8d7a-f03b88889f6a")
(ex (create-ns 'my-ns))
(ex (zipmap (range 15) (range 15)))
