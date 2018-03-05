(ns irida-wf-ga2xml.main
  (:require
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.string :as s]
    [irida-wf-ga2xml.core :refer [to-wf-vec]]
    [irida-wf-ga2xml.util :refer [vec->indented-xml]]
    )
  (:gen-class))

(def cli-options
  [["-t" "--analysis-type ANALYSIS_TYPE" "IRIDA AnalysisType"
    :default "DEFAULT"
    :validate [#(re-matches #"[\w\-]+" %) "Can only contain word characters"]]
   ["-W" "--workflow-version WORKFLOW_VERSION" "Workflow version"
    :default "0.1.0"
    :validate [#(re-matches #"[\d\.]+" %) "Can only contain numbers and periods"]]
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
      (let [{:keys [analysis-type input workflow-version multi-sample]} options
            xml-str (vec->indented-xml (to-wf-vec input
                                          :wf-version workflow-version
                                          :analysis-type analysis-type
                                          :single-sample? (not multi-sample)
                                          ))]
        (println xml-str)
        ))))