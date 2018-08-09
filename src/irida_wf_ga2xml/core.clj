(ns irida-wf-ga2xml.core
  (:require
    [irida-wf-ga2xml.util :refer :all]
    [irida-wf-ga2xml.messages :refer [tool-step->param-attr-map
                                      get-tool-param-messages
                                      default-workflow-messages
                                      prepend-param-details]]
    [clojure.data.json :as json]))

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
  [step]
  (if-let [outputs (get step "post_job_actions")]
    (let [renames (->> outputs
                       vals
                       (filter #(= (get % "action_type") "RenameDatasetAction"))
                       (map (fn [{:strs [action_arguments]}]
                              (let [filename (get action_arguments "newname")]
                                {:name     (rm-file-ext filename)
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
  [step & {:keys [get-param-labels?] :or {get-param-labels? true}}]
  (let [{:strs [tool_id id]} step
        {:keys [name]} (tool-id->repo-info tool_id)
        params (tool-params-map step)
        xml-vec (vec (map (fn [[k v]]
                            [:parameter
                             {:name         (parameter-name (if name name tool_id) id k)
                              :defaultValue v}
                             [:toolParameter
                              {:toolId        tool_id
                               :parameterName k}]])
                          params))
        out {:xml-vec xml-vec}]
    (if (and get-param-labels? (not-empty xml-vec))
      (conj out (get-tool-param-messages step (keys params)))
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
                          ^Boolean output-messages?]
                   :or   {single-sample? true
                          wf-version     "0.1.0"
                          analysis-type  "DEFAULT"
                          wf-name        nil
                          output-messages? true}}]
  (let [j (parse-ga path)
        {:strs [name annotation]} j
        name (if wf-name wf-name name)
        input (input-steps j)
        steps (tool-steps j)
        parameters-maps (->> steps
                       (map #(tool-params-vec % :get-param-labels? output-messages?))
                       (filter not-empty))
        out {:xml-vec
             [:iridaWorkflow
              [:id (uuid)]
              [:name name]
              [:version wf-version]
              [:analysisType analysis-type]
              [:inputs
               (when (paired? input)
                 [:sequenceReadsPaired (input-name input)])
               (when (needs-reference? j)
                 [:reference "reference"])
               [:requiresSingleSample (str single-sample?)]]
              (vcons :parameters (vec (mapcat :xml-vec parameters-maps)))
              (vcons :outputs (vec (filter not-empty (mapcat get-step-outputs steps))))
              (vcons :toolRepositories (vec (set (remove nil? (map tool-repo steps)))))]}
        ]
    (if output-messages?
      (conj out {:props (conj [(default-workflow-messages name analysis-type annotation)]
                              (->> parameters-maps
                                   (map :props)
                                   (map #(prepend-param-details name %))
                                   (remove nil?)
                                   (remove empty?)
                                   (vec)))})
      out)))
