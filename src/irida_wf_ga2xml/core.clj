(ns irida-wf-ga2xml.core
  (:require
    [irida-wf-ga2xml.util :refer :all]
    [irida-wf-ga2xml.messages :as msgs]
    [clojure.data.json :as json]
    [taoensso.timbre :as timbre
     :refer [log trace debug info warn error fatal report
             logf tracef debugf infof warnf errorf fatalf reportf
             spy get-env]]))

(def excluded-keys #{"__page__"
                     "__class__"
                     "input_type"
                     "__rerun_remap_job_id__"
                     "options"
                     "input"
                     "outputs"})

(defn get-step-outputs
  "Get all workflow step outputs that have been renamed. A workflow step output must be renamed
  to signify that it will be returned as an output from the workflow."
  [step & {:keys [remove-file-ext?] :or {remove-file-ext? false}}]
  (if-let [outputs (get step "post_job_actions")]
    (let [renames (->> outputs
                       vals
                       (filter #(= (get % "action_type") "RenameDatasetAction"))
                       (map (fn [{:strs [action_arguments]}]
                              (let [filename (get action_arguments "newname")]
                                {:name     (if remove-file-ext?
                                             (rm-file-ext filename)
                                             filename)
                                 :fileName filename}))))]
      (map #(conj [:output] %) renames))))

(defn tool-params-map
  "Get the tool parameters for a workflow step from the `tool_state` map.
  Flatten nested parameters by joining nested keys on '.' when a scalar value is reached.
  e.g {\"a\" {\"b\" {\"c\" 1} \"d\" 2} \"e\" 3}
  will produce the following map
  {\"a.b.c\" 1 \"a.d\" 2 \"e\" 3}"
  [step]
  (if-let [{:strs [tool_state]} step]
    (let [tool_state (json/read-str tool_state)
          ks (remove #(in? excluded-keys %) (keys tool_state))
          params (into {} (map (fn [[k v]] {k (try-parse-json v)}) (select-keys tool_state ks)))]
      (->> params
           (remove (fn [[_ v]] (nil? v)))
           (mapcat (fn [[k v]]
                     (if (map? v)
                       (flatten (recur-descend-maps k v))
                       {k v})))
           (into {})
           (filter (fn [[_ v]] (string? v)))
           (into {})))))

(defn tool-params-vec
  "Return a vector of all tool parameters for a workflow `step` in a format that will
  serialize into the expected IRIDA XML format for tool parameters."
  [step & {:keys [get-param-labels?
                  extra-tool-param-attrs?]
           :or   {get-param-labels?       true
                  extra-tool-param-attrs? false}}]
  (let [{:strs [tool_id id]} step
        {:keys [name]} (tool-id->repo-info tool_id)
        params (tool-params-map step)
        param-attrs (if get-param-labels?
                      (msgs/get-tool-param-values step (keys params))
                      nil)
        xml-vec (vec (map (fn [[k v]]
                            (let [base-attrs {:toolId        tool_id
                                              :parameterName k}
                                  extra-attrs (if extra-tool-param-attrs?
                                                (map (fn [x]
                                                       {x
                                                        (get-in param-attrs [:attrs x k])})
                                                     (keys (:attrs param-attrs)))
                                                nil)]
                              [:parameter
                               {:name         (parameter-name (if name name tool_id) id k)
                                :defaultValue v}
                               [:toolParameter
                                (->> (apply conj base-attrs extra-attrs)
                                     (remove (fn [[_ v]] (nil? v)))
                                     (into {}))
                                ]]))
                          params))
        out {:xml-vec xml-vec}]
    (if (and get-param-labels? (not-empty xml-vec))
      (conj out param-attrs)
      out)))

(defn to-wf-vec
  "Build an IRIDA workflow description vector from a Galaxy workflow ga JSON file.

  Args:
    `path` (String): a Galaxy workflow file path
    `single-sample?`: does the workflow operate on single samples? or multiple samples?
    `wf-version`: workflow version
    `analysis-type`: IRIDA AnalysisType enum

  Returns:
    Vector representation of IRIDA workflow XML"
  [^String path & {:keys [^Boolean single-sample?
                          ^String wf-version
                          ^String analysis-type
                          ^String wf-name
                          ^String wf-id
                          ^Boolean output-messages?
                          ^Boolean extra-tool-param-attrs?
                          ^Boolean remove-output-name-file-ext?]
                   :or   {single-sample?               true
                          wf-version                   "0.1.0"
                          analysis-type                "DEFAULT"
                          wf-name                      nil
                          wf-id                        (uuid)
                          output-messages?             true
                          extra-tool-param-attrs?      false
                          remove-output-name-file-ext? false}}]
  (let [ga-map (parse-ga path)
        {:strs [name annotation]} ga-map
        _ (info (str "Parsed Galaxy workflow file with name='" name "' and annotation/description='" annotation "'"))
        name (safe-workflow-name (if wf-name wf-name name))
        _ (info (str "Using worklfow name='" name "'"))
        input (input-steps ga-map)
        _ (info (count input) "input steps in workflow")
        steps (tool-steps ga-map)
        _ (info (count steps) "tool execution steps in workflow")
        parameters-maps (->> steps
                             (map #(tool-params-vec %
                                                    :get-param-labels? output-messages?
                                                    :extra-tool-param-attrs? extra-tool-param-attrs?))
                             (filter not-empty))
        out {:name name
             :xml-vec
             [:iridaWorkflow
              [:id wf-id]
              [:name name]
              [:version wf-version]
              [:analysisType analysis-type]
              [:inputs
               (when (paired? input)
                 [:sequenceReadsPaired (input-name input)])
               (when (needs-reference? ga-map)
                 [:reference "reference"])
               [:requiresSingleSample (str single-sample?)]]
              (vcons :parameters (vec (mapcat :xml-vec parameters-maps)))
              (vcons :outputs (->> steps
                                   (mapcat
                                     #(get-step-outputs %
                                                        :remove-file-ext? remove-output-name-file-ext?))
                                   (filter not-empty)
                                   (vec)))
              (vcons :toolRepositories (vec (set (remove nil? (map tool-repo steps)))))]}
        ]
    (if output-messages?
      (conj out {:props (conj [(msgs/default-workflow-messages name analysis-type annotation)]
                              (->> parameters-maps
                                   (map :props)
                                   (map #(msgs/prepend-param-details name %))
                                   (remove nil?)
                                   (remove empty?)
                                   (vec)))})
      out)))
