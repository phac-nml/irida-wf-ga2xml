(ns irida-wf-ga2xml.core
  (:require
    [irida-wf-ga2xml.util :refer :all]
    [clojure.data.json :as json]))

(def excluded-keys #{"__page__"
                     "__class__"
                     "input_type"
                     "__rerun_remap_job_id__"
                     "options"
                     "input"
                     "outputs"})

(def tool-id-repos-pattern (re-pattern "/repos/"))
(def slash-pattern (re-pattern "/"))

(defn dundered?
  "Is `x` wrapped in double underscores?"
  [x]
  (try
    (boolean (re-matches #"^__.*__$" x))
    (catch Exception _
      false)))

(defn try-parse-json [x]
  "Try to parse `x` as JSON string."
  (try
    (json/read-str x)
    (catch Exception _
      x)))

(defn recur-descend-maps
  "Flatten a nested map with '.' delimited keys to scalar values.
  e.g {\"a\" {\"b\" {\"c\" 1} \"d\" 2} \"e\" 3}
  will produce the following map
  {\"a.b.c\" 1 \"a.d\" 2 \"e\" 3}"
  [prev-key x]
  (if (map? x)
    (map (fn [[k v]]
           (if (map? x)
             (recur-descend-maps (str prev-key "." k) v)
             {(str prev-key "." k) v}))
         (remove (fn [[k _]] (dundered? k)) x))
    {prev-key x}))

(defn tool-id->repo-info
  "Get Tool Shed repo information from the Galaxy tool id if possible.
  Check if the tool id contains '/repos/' string."
  [tool-id]
  (if (re-find tool-id-repos-pattern tool-id)
    (let [[url & owner-tool-rest] (clojure.string/split tool-id tool-id-repos-pattern)
          [owner name & _] (clojure.string/split (first owner-tool-rest) slash-pattern)]
      (kw-quote url owner name))
    {:url nil :owner nil :name nil}))

(defn get-step-outputs
  "Get all workflow step outputs that have been renamed. A workflow step output must be renamed
  to signify that it will be returned as an output from the workflow."
  [step]
  (if-let [outputs (get step "post_job_actions")]
    (let [renames (->> outputs
                       vals
                       (filter #(= (get % "action_type") "RenameDatasetAction"))
                       (map (fn [{:strs [action_arguments output_name]}]
                              {:name     output_name
                               :fileName (get action_arguments "newname")})))]
      (map #(conj [:output] %) renames))))

(defn tool-repo
  "Get tool repository information for the tool at the given `step`.
  If no 'tool_shed_repository' map exists in the `step` map, then try to get
  the latest tool revision from the tool's Galaxy Tool Shed.
  A warning comment is added if the latest revision is used."
  [step]
  (let [{:strs [tool_id
                tool_shed_repository]} step
        {:strs [changeset_revision owner tool_shed]} tool_shed_repository
        url-owner-map (tool-id->repo-info tool_id)
        tool_shed (if tool_shed
                    tool_shed
                    (:url url-owner-map))
        name (:name url-owner-map)
        owner (if owner
                owner
                (:owner url-owner-map))
        url (if (has-http? tool_shed)
              tool_shed
              (str "https://" tool_shed))
        revision (if changeset_revision
                   changeset_revision
                   ; try to get the tool revision via https then http if that fails
                   (if-let [r (revision-from-url (galaxy-shed-url tool_id))]
                     r
                     (revision-from-url (galaxy-shed-url tool_id :https? false))))
        repo-info (vec (kw-quote name owner url revision))]
    (if name
      (vcons
        :repository
        (if changeset_revision
          repo-info
          (conj repo-info
                [:-comment
                 (str "WARNING: Latest revision fetched from "
                      (galaxy-shed-url tool_id))])))
      nil)))

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
  [step]
  (let [{:strs [tool_id id]} step
        {:keys [name]} (tool-id->repo-info tool_id)
        params (tool-params-map step)]
    (vec (map (fn [[k v]]
                [:parameter {:name         (str (if name
                                                  name
                                                  tool_id)
                                                "-" id
                                                "-" k)
                             :defaultValue v}
                 [:toolParameter {:toolId        tool_id
                                  :parameterName k}]])
              params))))

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
                          ^String wf-name]
                   :or   {single-sample? true
                          wf-version     "0.1.0"
                          analysis-type  "DEFAULT"
                          wf-name nil}}]
  (let [j (parse-ga path)
        {:strs [name]} j
        input (input-steps j)
        steps (tool-steps j)]
    [:iridaWorkflow
     [:id (uuid)]
     [:name (if wf-name wf-name name)]
     [:version wf-version]
     [:analysisType analysis-type]
     [:inputs
      (when (paired? input)
        [:sequenceReadsPaired "sequence_reads_paired"])
      (when (needs-reference? j)
        [:reference "reference"])
      [:requiresSingleSample (str single-sample?)]]
     (vcons :parameters (vec (filter not-empty (mapcat tool-params-vec steps))))
     (vcons :outputs (vec (filter not-empty (mapcat get-step-outputs steps))))
     (vcons :toolRepositories (vec (remove nil? (map tool-repo steps))))]))
