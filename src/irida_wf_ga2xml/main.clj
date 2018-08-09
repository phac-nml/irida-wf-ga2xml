(ns irida-wf-ga2xml.main
  (:require
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.string :as s]
    [irida-wf-ga2xml.core :refer [to-wf-vec]]
    [irida-wf-ga2xml.util :refer [vec->indented-xml]]
    [irida-wf-ga2xml.messages :as msgs]
    [clojure.java.io :as io])
  (:gen-class))

(def cli-options
  [["-n" "--workflow-name WORKFLOW_NAME" "Workflow name (default is to extract name from workflow input file)"
    :default nil]
   ["-t" "--analysis-type ANALYSIS_TYPE" "IRIDA AnalysisType"
    :default "DEFAULT"
    :validate [#(re-matches #"[\w\-]+" %) "Can only contain word characters"]]
   ["-W" "--workflow-version WORKFLOW_VERSION" "Workflow version"
    :default "0.1.0"
    :validate [#(re-matches #"[\d\.]+" %) "Can only contain numbers and periods"]]
   ["-o" "--outdir OUPUT_DIRECTORY"
    "Output directory; where to create the <workflow_name>/<version>/<files> directory structure"
    :default nil]
   ["-m" "--multi-sample" "Multiple sample workflow; not a single sample workflow"]
   ["-i" "--input INPUT" "Galaxy workflow ga JSON format file"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Output IRIDA workflow XML file from Galaxy Workflow ga JSON file"
        ""
        "Usage: irida_wf_ga2xml [options] > irida_workflow.xml"
        ""
        "Options:"
        options-summary
        ""]
       (s/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))

(defn validate-args
  [args]
  (let [parsed-args (parse-opts args cli-options)
        {:keys [options arguments errors summary]} parsed-args]
    (cond
      (:help options) {:exit-message (usage summary) :ok? true}
      errors {:exit-message (error-msg errors)}
      (:input options) {:action "parse" :options options}
      :else {:exit-message (usage summary)}
      )))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main
  ""
  [& args]
  (let [{:keys [action options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (let [{:keys [analysis-type
                    input
                    outdir
                    workflow-version
                    multi-sample
                    workflow-name]} options
            irida-wf-map (to-wf-vec input
                                    :wf-version workflow-version
                                    :analysis-type analysis-type
                                    :single-sample? (not multi-sample)
                                    :wf-name workflow-name)
            xml-str (vec->indented-xml (:xml-vec irida-wf-map))]
        (if outdir
          (let [file-with-base (partial io/file outdir workflow-name workflow-version)
                irida-xml-filename (.toString (file-with-base "irida_workflow.xml"))
                ga-file-dest (.toString (file-with-base "irida_workflow_structure.ga"))]
            (io/make-parents irida-xml-filename)
            (io/make-parents ga-file-dest)
            (spit irida-xml-filename xml-str)
            (println "Wrote workflow XML to " irida-xml-filename)
            (spit ga-file-dest (slurp input))
            (println "Wrote Galaxy workflow *.ga file to " ga-file-dest)
            (if-let [[main-props tool-param-props] (:props irida-wf-map)]
              (let [msg-props-file (.toString (file-with-base "messages_en.properties"))]
                (io/make-parents msg-props-file)
                (msgs/write-props msg-props-file main-props tool-param-props)
                (println "Wrote IRIDA messages to " msg-props-file))))
          ;; no outdir specified? print to stdout
          (println xml-str))))))
