(ns irida-wf-ga2xml.main
  (:require
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.string :as s]
    [irida-wf-ga2xml.core :refer [to-wf-vec]]
    [irida-wf-ga2xml.util :refer [vec->indented-xml]]
    [irida-wf-ga2xml.messages :as msgs]
    [clojure.java.io :as io]
    [taoensso.timbre :as timbre
     :refer [log  trace  debug  info  warn  error  fatal  report
             logf tracef debugf infof warnf errorf fatalf reportf
             spy get-env]])
  (:gen-class))

(def --program-- "irida-wf-ga2xml")
(def --version-- "1.1.0")

(def cli-options
  [["-n" "--workflow-name WORKFLOW_NAME" "Workflow name (default is to extract name from workflow input file)"
    :default nil]
   ["-t" "--analysis-type ANALYSIS_TYPE" "IRIDA AnalysisType"
    :default "DEFAULT"
    :validate [#(re-matches #"[\w\-]+" %) "Can only contain word characters"]]
   ["-W" "--workflow-version WORKFLOW_VERSION" "Workflow version"
    :default "0.1.0"
    :validate [#(re-matches #"[\d\.]+" %) "Can only contain numbers and periods"]]
   ["-I" "--workflow-id WORKFLOW_ID" "Workflow ID"
    :default nil]
   ["-o" "--outdir OUPUT_DIRECTORY"
    "Output directory; where to create the <workflow-name>/<workflow-version>/ directory structure and write the 'irida_workflow.xml', 'irida_workflow_structure.ga' and 'messages_en.properties' files"
    :default nil]
   ["-m" "--multi-sample" "Multiple sample workflow; not a single sample workflow"]
   ["-i" "--input INPUT" "Galaxy workflow ga JSON format file"]
   ["-x" "--extra-tool-param-attrs" "Save extra toolParameter attributes [\"label\", \"type\"] to XML"]
   [nil "--remove-output-name-file-ext" "Remove file extension in workflow output names?"]
   ["-v" "--verbosity" "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-V" "--version" "Display version"]
   ["-h" "--help"]])

(defn program-version []
  (str --program-- " v" --version--))

(defn usage [options-summary]
  (->> [(str
          (program-version)
          ": Output an IRIDA workflow directory with irida_structure.xml, messages_en.properties and irida_workflow_structure.ga files from a Galaxy Workflow *.ga JSON file")
        ""
        (str "Usage: " --program-- " [options]")
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
      (:version options) {:exit-message (program-version) :ok? true}
      errors {:exit-message (error-msg errors)}
      (:input options) {:action "parse" :options options}
      :else {:exit-message (usage summary)}
      )))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn verbosity->logging-level
  [^Long verbosity]
  (get (->> timbre/-levels-vec
            (reverse)
            ; drop :report and :fatal - verbosity 0 == :error level
            (drop 2)
            (vec))
       verbosity))

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
                    workflow-id
                    multi-sample
                    workflow-name
                    extra-tool-param-attrs
                    remove-output-name-file-ext
                    verbosity]} options
            _ (timbre/set-level! (verbosity->logging-level verbosity))
            _ (trace "Options" options)
            _ (info "Parsing " input " Galaxy workflow file. Creating irida_workflow.xml with workflow name '" workflow-name "' and version '" workflow-version "'.")
            irida-wf-map (to-wf-vec input
                                    :wf-id workflow-id
                                    :wf-version workflow-version
                                    :analysis-type analysis-type
                                    :single-sample? (not multi-sample)
                                    :wf-name workflow-name
                                    :extra-tool-param-attrs? (boolean extra-tool-param-attrs)
                                    :remove-output-name-file-ext? (boolean remove-output-name-file-ext))
            _ (trace "irida-wf-map: " irida-wf-map)
            xml-str (vec->indented-xml (:xml-vec irida-wf-map))]
        (if outdir
          (let [file-with-base (partial io/file outdir (:name irida-wf-map) workflow-version)
                irida-xml-filename (.toString (file-with-base "irida_workflow.xml"))
                ga-file-dest (.toString (file-with-base "irida_workflow_structure.ga"))]
            (io/make-parents irida-xml-filename)
            (io/make-parents ga-file-dest)
            (spit irida-xml-filename xml-str)
            (info "Wrote workflow XML to " irida-xml-filename)
            (spit ga-file-dest (slurp input))
            (info "Wrote Galaxy workflow *.ga file to " ga-file-dest)
            (if-let [[main-props tool-param-props] (:props irida-wf-map)]
              (let [msg-props-file (.toString (file-with-base "messages_en.properties"))]
                (trace "main workflow properties" main-props)
                (trace "tool parameter properties" tool-param-props)
                (io/make-parents msg-props-file)
                (msgs/write-props msg-props-file main-props tool-param-props)
                (info "Wrote IRIDA messages to " msg-props-file))
              (warn "No messages_en.properties written!")))
          (do
            (warn "No output directory specified. Printing irida_structure.xml to standard output")
            (println xml-str)))))))
