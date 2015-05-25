(defproject com.2tothe8th/example "0.5.0+repl"
  :description "Add example fn calls to your code and generate tests from those examples."
  :url "https://github.com/dubiousdavid/example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [stch-library/glob "0.2.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
  :codox {:src-dir-uri "https://github.com/dubiousdavid/example/blob/master"
          :src-linenum-anchor-prefix "L"
          :output-dir "."})
