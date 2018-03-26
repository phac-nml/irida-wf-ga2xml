(ns irida-wf-ga2xml.util
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.data.xml :as xml :refer [element emit emit-str sexp-as-element indent-str]]))

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
  (let [steps (get-steps j)]
    (->> steps
         (filter step-type-tool?)
         (sort-by get-id))))

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

(defn vec->indented-xml
  ""
  [v]
  (indent-str
    (sexp-as-element v)))