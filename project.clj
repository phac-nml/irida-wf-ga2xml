(defproject irida-wf-ga2xml "1.2.0"
  :description "Parse a Galaxy workflow ga JSON file and output an IRIDA workflow description XML file"
  :url "https://github.com/phac-nml/irida-wf-ga2xml"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.2.0-alpha5"]
                 [org.clojure/data.zip "0.1.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [cheshire "5.9.0"]]
  :main ^:skip-aot irida-wf-ga2xml.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :repl {:plugins [[cider/cider-nrepl "0.16.0"]]}}
  :repl-options {:host "0.0.0.0"
                 :port 36919})
