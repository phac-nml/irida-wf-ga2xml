(ns irida-wf-ga2xml.util
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.data.xml :as xml :refer [element emit emit-str sexp-as-element indent-str]]
            [clojure.data.json :as json])
  (:import (clojure.lang Keyword PersistentVector)))

(def tool-id-repos-pattern (re-pattern "/repos/"))
(def slash-pattern (re-pattern "/"))

(defmacro kw-quote
  "Take one or more symbols and map-ify them.
  Example:
  (def name \"hi\")
  (def value 1)
  (def other_thing \"this is something\")
  (kw-quote name value other_thing)
  => {:name \"hi\" :value 1 :other_thing \"this is something\"}"
  ([x] `{(keyword (quote ~x)) ~x})
  ([x & xs]
   `(into (kw-quote ~x) (kw-quote ~@xs))))

(defn vcons
  "Prepend `x` to vector `coll`"
  [x coll]
  (vec (cons x coll)))

(defn in?
  "Is item `x` in collection `xs`?"
  [xs x]
  (some #(= x %) xs))

(defn kw->str [kw]
  (condp = (class kw)
    String kw
    Keyword (->> (str kw) vec rest (apply str))
    (throw (IllegalArgumentException. (str "Arg 'kw' is not a Clojure Keyword! class=" (class kw))))))

(defn safe-workflow-name
  "Remove whitespace and other troublesome characters from the workflow name.
  `wf-name` is the workflow name to make safe.
  `repl` is the replacement character/string.
  Returns a safe formatted workflow name."
  [wf-name & {:keys [repl] :or {repl ""}}]
  (clojure.string/replace wf-name #"[^\w\-]+" repl))

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
           (let [new-key (str (kw->str prev-key) "." (kw->str k))]
             (if (map? x)
               (recur-descend-maps new-key v)
               {new-key v})))
         (remove (fn [[k _]] (dundered? k)) x))
    {prev-key x}))

(defn uuid
  "Random UUID using Java stdlib function"
  [] (str (java.util.UUID/randomUUID)))

(defn rm-file-ext
  "Remove file extension from filename. 'filename.txt' -> 'filename'"
  [s]
  (clojure.string/replace s #"\.\w+$" ""))

(defn parse-ga
  "Parse Galaxy workflow ga file as JSON into map"
  [path]
  (json/read (io/reader path)))

(defn get-id [x]
  (get x "id"))

(defn get-steps
  "Get workflow steps ordered by 'id'"
  [{:strs [steps]}]
  (->> steps
       vals
       (sort-by get-id)))

(defn step-type?
  "Is workflow step type `step-type`?"
  [step-type {:strs [type]}]
  (= type step-type))

(def step-type-tool? (partial step-type? "tool"))
(def step-type-data_collection_input? (partial step-type? "data_collection_input"))
(def step-type-data_input? (partial step-type? "data_input"))

(defn tool-id->repo-info
  "Get Tool Shed repo information from the Galaxy tool id if possible.
  Check if the tool id contains '/repos/' string."
  [tool-id]
  (if (re-find tool-id-repos-pattern tool-id)
    (let [[url & owner-tool-rest] (clojure.string/split tool-id tool-id-repos-pattern)
          [owner name & _] (clojure.string/split (first owner-tool-rest) slash-pattern)]
      (kw-quote url owner name))
    {:url nil :owner nil :name nil}))

(defn step-input-name-reference?
  "Is workflow step reference input file for a workflow that requires a reference file? (e.g. SNVPhyl)"
  [{:strs [tool_state]}]
  (let [tool_state (json/read-str tool_state)
        {:strs [name]} tool_state]
    (= name "reference")))

(defn input-steps
  "Get \"data_input\"/\"data_collection_input\" type workflow steps.
  Remove \"reference\" input step if present.
  Return vector if more than one. Scalar if only one input step."
  [j]
  (let [steps (get-steps j)
        inputs (->> steps
                    (remove step-input-name-reference?)
                    (filter #(or (step-type-data_collection_input? %)
                                 (step-type-data_input? %))))]
    (if (= (count inputs) 1)
      (first inputs)
      inputs)))

(defn tool-steps
  "Get \"tool\" type workflow steps order ordered by 'id'"
  [j]
  (->> j
       (get-steps)
       (filter step-type-tool?)
       (sort-by get-id)))

(defn reference-step
  "Get the workflow reference step as a map if it exists in workflow map `j`, nil otherwise"
  [j]
  (let [steps (get-steps j)]
    (->> steps
         (filter step-type-data_input?)
         (filter step-input-name-reference?)
         first)))

(defn needs-reference?
  "Does a workflow need a reference? Does it have a reference step?"
  [j]
  (not (nil? (reference-step j))))

(defn paired?
  "Paired input required for workflow? (e.g. paired end reads)"
  [{:strs [tool_state]}]
  (let [tool_state (json/read-str tool_state)
        {:strs [collection_type]} tool_state]
    (= collection_type "list:paired")))

(defn input-name
  "Name attribute of an input step from 'tool_state' map."
  [{:strs [tool_state]}]
  (let [tool_state (json/read-str tool_state)
        {:strs [name]} tool_state]
    name))

(defn parameter-name
  "IRIDA workflow XML tool parameter name.

  Example:
  ```clj
  (parameter-name \"prokka\" 6 \"genus\")
  ;;=> \"prokka-6-genus\"
  ```"
  [name-or-tool_id
   step-id
   param-name]
  (apply str (interpose "-" [name-or-tool_id
                             step-id
                             param-name])))

(defn has-http?
  "Does string `x` start with 'http'?"
  [x]
  (try
    (boolean (re-matches (re-pattern "^http.*") x))
    (catch NullPointerException _
      false)))

(defn galaxy-shed-url
  "Get the Galaxy Shed URL for a given tool id"
  [tool-id & {:keys [https?] :or {https? true}}]
  (let [base-url (second (re-matches #"^(.*)/\w+/[^/]+$" tool-id))]
    (str (if https? "https://" "http://") base-url)))

(defn revision-from-url
  "Try to get the latest tool revision hash from a `url`.
   Download the HTML for `url` and look for first revision hash with
   12 [a-f0-9] characters after '/rev/'"
  [url]
  (try
    (let [html (slurp url)]
      (second (re-find #"/rev/([a-f0-9]{12})" html)))
    (catch Exception _
      nil)))

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

(defn ^String vec->indented-xml
  "Vector to indented XML string"
  [^PersistentVector v]
  (indent-str
    (sexp-as-element v)))