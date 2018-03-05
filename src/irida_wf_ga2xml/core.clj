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

(defn dundered?
  "Is `x` wrapped in double underscores?"
  [x]
  (try
    (boolean (re-matches #"^__.*__$" x))
    (catch Exception _
      false)))

(defn try-parse-json [x]
  (try
    (json/read-str x)
    (catch Exception _
      x)))

(defn recur-descend-maps [prev-key x]
  (if (map? x)
    (map (fn [[k v]]
           (if (map? x)
             (recur-descend-maps (str prev-key "." k) v)
             {(str prev-key "."  k) v}))
         (remove (fn [[k _]] (dundered? k)) x))
    {prev-key x}))

(defn tool-id->repo-info [tool-id]
  (let [[url & owner-tool-rest] (clojure.string/split tool-id (re-pattern "/repos/"))
        [owner name & _] (clojure.string/split (first owner-tool-rest) (re-pattern "/"))]
    (kw-quote url owner name)))

(defn get-step-outputs [step]
  (if-let [outputs (get step "post_job_actions")]
    (let [renames (->> outputs
                       vals
                       (filter #(= (get % "action_type") "RenameDatasetAction"))
                       (map (fn [{:strs [action_arguments output_name]}]
                              {:name     output_name
                               :fileName (get action_arguments "newname")})))]
      (map #(conj [:output] %) renames))))

(defn tool-repo [step]
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
        url (if (has-http? tool_shed) tool_shed (str "https://" tool_shed))
        revision (if changeset_revision
                   changeset_revision
                   (if-let [r (revision-from-url (galaxy-shed-url tool_id))]
                     r
                     (revision-from-url (galaxy-shed-url tool_id :https? false))))
        repo-info (vec (kw-quote name owner url revision))]
    (vcons
      :repository
      (if changeset_revision
        repo-info
        (conj repo-info
              [:-comment (str "WARNING: Latest revision fetched from " (galaxy-shed-url tool_id))])
        ))))

#_(let [wf (parse-ga "test/data/snvphyl-1.0.1-workflow.ga")
      step (get-in wf ["steps" "8"])]
  (if-let [{:strs [tool_state]} step]
    (let [tool_state (json/read-str tool_state)
          ks (remove #(in? excluded-keys %) (keys tool_state))
          params (into {} (map (fn [[k v]] {k (try-parse-json v)}) (select-keys tool_state ks)))
          ]
      (->> params
           (remove (fn [[_ v]] (nil? v)))
           (mapcat (fn [[k v]]
                     (if (map? v)
                       (flatten (recur-descend-maps k v))
                       {k v})))
           (into {})
           (filter (fn [[_ v]] (string? v)))
           (into {})))))

(defn tool-params-map [step]
  (if-let [{:strs [tool_state]} step]
    (let [tool_state (json/read-str tool_state)
          ks (remove #(in? excluded-keys %) (keys tool_state))
          params (into {} (map (fn [[k v]] {k (try-parse-json v)}) (select-keys tool_state ks)))
          ]
      (->> params
           (remove (fn [[_ v]] (nil? v)))
           (mapcat (fn [[k v]]
                     (if (map? v)
                       (flatten (recur-descend-maps k v))
                       {k v})))
           (into {})
           (filter (fn [[_ v]] (string? v)))
           (into {})))))

(defn tool-params-vec [step]
  (let [{:strs [tool_id]} step
        {:keys [name]} (tool-id->repo-info tool_id)
        params (tool-params-map step)]
    (vec (map (fn [[k v]]
                [:parameter {:name (str name "-" k)
                             :defaultValue v}
                 [:toolParameter {:toolId tool_id
                                  :parameterName k}]])
              params))))

#_(let [wf (parse-ga "test/data/snvphyl-1.0.1-workflow.ga")
      {:strs [name steps]} wf
      steps (dissoc steps "0")
      step-keys (sort (keys steps))
      ordered-steps (map #(get steps %) step-keys)]
  (map tool-params-vec ordered-steps))

(defn to-wf-vec
  [path & {:keys [single-sample?
                  wf-version
                  analysis-type]
           :or   {single-sample? true
                  wf-version     "0.1.0"
                  analysis-type "DEFAULT"}}]
  (prn "single sample?" single-sample?)
  (let [j (parse-ga path)
        input (input-steps j)
        steps (tool-steps j)]
    [:iridaWorkflow
     [:id (uuid)]
     [:name name]
     [:version wf-version]
     [:analysisType analysis-type]
     [:inputs
      (when (paired? input)
        [:sequenceReadsPaired "sequence_reads_paired"])
      (when (needs-reference? j)
        [:reference "reference"])
      [:requiresSingleSample (str single-sample?)]]
     (vcons :parameters (vec (mapcat tool-params-vec steps)))
     (vcons :outputs (vec (mapcat get-step-outputs steps)))
     (vcons :toolRepositories (vec (map tool-repo steps)))]))

(let [path "test/data/snvphyl-1.0.1-workflow.ga"
      single-sample? false
      wf-version "9000.x.x"
      analysis-type "fuck"]
  (prn "single sample?" single-sample?)
  (let [j (parse-ga path)
        input (input-steps j)
        steps (tool-steps j)]
    (prn "input" (type input) (count input) (paired? input))
    (prn "steps" (count steps))
    (prn "ref?" (needs-reference? j))
    (prn (mapcat tool-params-vec steps))
    (prn "params" (count (mapcat tool-params-vec steps)))
    [:iridaWorkflow
     [:id (uuid)]
     [:name name]
     [:version wf-version]
     [:analysisType analysis-type]
     [:inputs
      (when (paired? input)
        [:sequenceReadsPaired "sequence_reads_paired"])
      (when (needs-reference? j)
        [:reference "reference"])
      [:requiresSingleSample (str single-sample?)]]
     (vcons :parameters (vec (mapcat tool-params-vec steps)))
     (vcons :outputs (vec (mapcat get-step-outputs steps)))
     (vcons :toolRepositories (vec (map tool-repo steps)))])
  )
