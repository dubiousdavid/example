(defproject com.2tothe8th/example "0.4.0+repl"
  :description "Add example fn calls to your code."
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [stch-library/glob "0.2.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
  :codox {:src-dir-uri ""
          :src-linenum-anchor-prefix "L"
          :output-dir "."})
