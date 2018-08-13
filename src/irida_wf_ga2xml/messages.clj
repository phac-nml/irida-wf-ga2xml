(ns irida-wf-ga2xml.messages
  "Functions for getting messages.properties entries given a Galaxy workflow spec file.
  - tool parameter information
  - workflow description"
  (:require
    [irida-wf-ga2xml.util :as util]
    [clojure.zip :as zip :refer [children xml-zip]]
    [clojure.data.xml :as xml]
    [clojure.data.zip.xml :as zip-xml :refer [xml-> attr]]
    [clojure.java.io :as io]
    [taoensso.timbre :as timbre
     :refer [log trace debug info warn error fatal report
             logf tracef debugf infof warnf errorf fatalf reportf
             spy get-env]])
  (:import
    [java.util Properties Map]
    (clojure.lang PersistentVector)))

(defn get-repo-info
  "Get tool repository informatio map given Galaxy workflow `tool-step` map.

  Returns map with keys:
  - `:name`: tool name (e.g. 'prokka')
  - `:owner`: owner name (e.g. 'uic')
  - `:url`: base toolshed url (e.g. 'https://toolshed.g2.bx.psu.edu')
  - `:revision`: tool revision hash string (e.g. 'eaee459f3d69')
  "
  [tool-step]
  (into {} (rest (util/tool-repo tool-step))))


(defn get-toolshed-browse-html
  "Get the HTML for a Galaxy tool's toolshed browse page given some `repo-info`.

  `repo-info` is a map with keys:
   - `:name`: tool name (e.g. 'prokka')
   - `:owner`: owner name (e.g. 'uic')
   - `:url`: base toolshed url (e.g. 'https://toolshed.g2.bx.psu.edu')
   - `:revision`: tool revision hash string (e.g. 'eaee459f3d69')
   "
  [repo-info]
  (let [{:keys [name owner url revision]} repo-info]
    (slurp (str url "/repos/" owner "/" name "/file/" revision))))

(defn xml-filename
  [html]
  (if-let [[_ filename] (re-find (re-pattern "class=\"filename\">\\s*<a href=\"\\S*\\/repos\\/\\w+\\/\\w+\\/file\\/\\w+\\/(\\w+\\.xml)\"") html)]
    filename
    nil))


(defn raw-file-xml-url
  "Get the raw XML file URL given `repo-info` map and `filename` string.

   Example:
   ```clj
   (raw-file-xml-url {:name \"sistr_cmd\"
                      :owner \"nml\"
                      :revision \"5c8ff92e38a9\"
                      :url \"https://toolshed.g2.bx.psu.edu\"}
                      \"sistr_cmd.xml\")
   ;;=> \"https://toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/raw-file/5c8ff92e38a9/sistr_cmd.xml\""
  [repo-info filename]
  (let [{:keys [name url owner revision]} repo-info]
    (str url "/repos/" owner "/" name "/raw-file/" revision "/" filename)))


(defn xml-url->zipper
  "Read and parse an XML file from a `url` and return a zipper for the parsed XML starting at the root element."
  [url]
  (-> url
      slurp
      xml/parse-str
      zip/xml-zip))

(defn name-kw
  "Get an XML `node` element's `:name` attribute as a keyword."
  [node]
  (keyword (attr node :name)))

(defn get-param-values
  "Given an Galaxy tool XML `zipper`, get potentially nested map of param tag name attribute to `get-attr` attribute values (default :label).

  This function recursively descends into :section and :conditional tags to search for :param tags to extract :name and :label (or other attrs) from. The potentially nested structure of the tool XML is reflected in the returned map.

  Examples:
  Given the tool XML for [biohansel](https://toolshed.g2.bx.psu.edu/repos/nml/bio_hansel/raw-file/4654c51dae72/bio_hansel.xml) and with `:get-attr :type`, returns the following map:

  ```clj
  {:kmer_vals {:kmer_min \"integer\"
               :kmer_max \"integer\"}
   :qc_vals {:low_cov_depth_freq \"integer\"
             :min_ambiguous_tiles \"integer\"
             :max_missing_tiles \"float\"
             :max_intermediate_tiles \"float\"
             :low_coverage_warning \"integer\"}
   :dev_args {:use_json \"boolean\"}
   :input {:type \"select\"
           :fasta \"data\"
           :forward \"data\"
           :reverse \"data\"
           :single \"data\"
           :paired_collection \"data_collection\"}
   :type_of_scheme {:scheme_type \"select\", :scheme_input \"data\"}}
  ```
  "
  [zipper & {:keys [tag-search attrs]
             :or   {tag-search [:tool :inputs]
                    attrs      :label}}]
  (letfn [(descend-children [el] (into {} (->>
                                            (for [x (children el)]
                                              (if (= (class x) String)
                                                nil
                                                (get-param-values (xml-zip x) :tag-search [] :attrs attrs)))
                                            (remove nil?)
                                            (flatten))))
          (iter-non-param-elements [tag-kw]
            (for [el (apply (partial xml-> zipper) (conj tag-search tag-kw))]
              {(name-kw el)
               (descend-children el)}))]
    (let [zippart (partial xml-> zipper)
          params (for [param (apply zippart (conj tag-search :param))]
                   {(name-kw param)
                    (attr param attrs)})
          sections (iter-non-param-elements :section)
          conditionals (iter-non-param-elements :conditional)]
      (into {} (concat params sections conditionals)))))




(defn flatten-param-values-map
  "Flatten a nested tool param values map.

  Example:
  ```clj
  (flatten-param-values-map
    {:section {:param1 \"A\"
               :param2 \"B\"}
     :conditional {:param3 \"C\"}
                   :param4 \"D\"})
  ;;=> {\"section.param1\" \"A\", \"section.param2\" \"B\", \"conditional.param3\" \"C\", \"param4\" \"D\"}
  "
  [m]
  (->> m
       (remove (fn [[_ v]] (nil? v)))
       (mapcat (fn [[k v]]
                 (let [str-k (util/kw->str k)]
                   (if (map? v)
                     (flatten (util/recur-descend-maps str-k v))
                     {str-k v}))))
       (into {})
       (filter (fn [[_ v]] (string? v)))
       (into {})))

(defn repo-info->param-attr-map
  [repo-info & {:keys [attrs]
                :or   {attrs [:label :type]}}]
  (try
    (let [zipper (->> repo-info
                      (get-toolshed-browse-html)
                      (xml-filename)
                      (raw-file-xml-url repo-info)
                      (xml-url->zipper))]
      (into {} (map
                 (fn [attr]
                   {attr
                    (-> zipper
                        (get-param-values :attrs attr)
                        (flatten-param-values-map))})
                 attrs)))
    (catch Exception e
      (error "Could not get tool parameter attribute info for" repo-info "; Encountered error:" e)
      nil)))

(defn tool-step->param-attr-map
  [tool-step & {:keys [attrs]
                :or   {attrs [:label :type]}}]
  (repo-info->param-attr-map (get-repo-info tool-step) :attrs attrs))

(defn tool-param-properties-key
  [tool-name step-id [param-name param-label]]
  [(util/parameter-name tool-name step-id param-name)
   param-label])

(defn get-tool-param-values
  "Given a tool `step`, get the tool param attribute values from the Galaxy tool
  XML file param key names specified in `param-names`.
  Returns a map of `{:props {:label {:param-key \"param label value\" ...}}`"
  [step param-names & {:keys [attrs] :or {attrs [:label :type]}}]
  (let [{:strs [tool_id id]} step
        attrs-map (tool-step->param-attr-map step :attrs attrs)
        repo-info (get-repo-info step)
        {:keys [name]} repo-info
        label-map-prop-keys (into {} (map #(tool-param-properties-key
                                             (if name
                                               name
                                               tool_id)
                                             id
                                             %)
                                          (:label attrs-map)))
        param-names (map #(util/parameter-name (if name name tool_id) id %) param-names)]
    {:props (->> param-names
                 (map #(if-let [found (find label-map-prop-keys %)]
                         found
                         [% ""]))
                 (remove nil?)
                 (into {}))
     :attrs attrs-map}))

(defn default-workflow-messages [name analysis-type description]
  {(str "workflow." analysis-type ".title")       (str name " Pipeline")
   (str "workflow." analysis-type ".description") description
   (str "pipeline.title." name)                   (str "Pipelines - " name)
   (str "pipeline.h1." name)                      (str name " Pipeline")
   (str "pipeline.parameters.modal-title." name)  (str name " Pipeline Parameters")})


(defn prepend-param-details
  "Prepend required tool parameter key prefixes for output to IRIDA compatible
  messages.properties.
  `wf-name` is the workflow name.
  `props` is a map of properties keys and values.
  Returns a map of properties keys with proper prefixes."
  [wf-name props]
  (into {} (map (fn [[k v]]
                  {(str "pipeline.parameters." wf-name "." k)
                   v})
                props)))


(defn ^Properties map->properties
  "Map to Properties object"
  [^Map m]
  (let [p (Properties.)]
    (doseq [[k v] m]
      (.setProperty p (name (str k)) (str v)))
    p))

(defn msg-key->tool-step-number-map
  "Given a messages Properties `prop-key` return a map of `:tool` name and workflow `:step-number`"
  [prop-key]
  (let [tool-step-param-str (->> (clojure.string/split prop-key #"\.")
                                 (drop 3)
                                 (first))
        [tool step-number _] (clojure.string/split tool-step-param-str #"-")]
    {:tool        tool
     :step-number step-number}))

(defn write-props
  "Write workflow info and tool parameter properties to an `outfile`.
  `main-props` is a map with the workflow info properties.
  `tool-param-props` is a vector of maps with tool specific parameter properties."
  [^String outfile
   ^Map main-props
   ^PersistentVector tool-param-props]
  (with-open [w (io/writer outfile)]
    (.store (map->properties main-props) w "Pipeline Info Properties"))
  (doseq [m tool-param-props]
    (let [{:keys [tool step-number]} (->> m
                                          (keys)
                                          (first)
                                          (msg-key->tool-step-number-map))]
      (with-open [w (io/writer outfile :append true)]
        (.store (map->properties m) w (str "Tool Parameters - Tool: " tool " - Workflow Step #: " step-number))))))
