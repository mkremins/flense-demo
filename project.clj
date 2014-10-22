(defproject mkremins/flense-demo "0.0-SNAPSHOT"
  :description "In-browser demo of Flense"
  :url "https://mkremins.github.io/flense-demo"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo}

  :dependencies
  [[org.clojure/clojure "1.6.0"]
   [org.clojure/clojurescript "0.0-2371"]
   [org.clojure/core.async "0.1.346.0-17112a-alpha"]
   [com.facebook/react "0.11.2"]
   [mkremins/flense "0.0-SNAPSHOT"]
   [om "0.7.3"]
   [spellhouse/phalanges "0.1.4"]]

  :plugins
  [[lein-cljsbuild "1.0.3"]]

  :cljsbuild
  {:builds [{:source-paths ["src"]
             :compiler {:preamble ["react/react.js"]
                        :output-to "target/flense_demo.js"
                        :source-map "target/flense_demo.js.map"
                        :optimizations :whitespace}}]})
