(defproject mkremins/flense-demo "0.0-SNAPSHOT"
  :description "In-browser demo of Flense"
  :url "https://mkremins.github.io/flense-demo"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo}

  :dependencies
  [[org.clojure/clojure "1.7.0"]
   [org.clojure/clojurescript "1.7.145"]
   [mkremins/flense "0.0-SNAPSHOT"]
   [org.omcljs/om "1.0.0-alpha3"]
   [prismatic/om-tools "0.4.0"]
   [spellhouse/phalanges "0.1.6"]]

  :plugins
  [[lein-cljsbuild "1.1.0"]]

  :cljsbuild
  {:builds [{:source-paths ["src"]
             :compiler {:main flense-demo.app
                        :output-to "target/app.js"
                        :output-dir "target"
                        :source-map true
                        :optimizations :none}}]})
