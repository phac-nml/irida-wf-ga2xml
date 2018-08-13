(ns irida-wf-ga2xml.util-test
  (:require [clojure.test :refer :all]
            [irida-wf-ga2xml.util :refer :all]))

(def snvphyl-ga "test/data/snvphyl-1.0.1-workflow.ga")
(def basic-ga "test/data/basic-sistr-fasta-workflow.ga")
(def asm-ga "test/data/irida_workflow_structure.ga")
(def masher-ga "test/data/refseq_masher.ga")

(deftest keyword-quote
  (let [something "else"
        cool "beans"
        awesome 1]
    (is (= (kw-quote something) {:something "else"}))
    (is (= (kw-quote something cool awesome)
           {:something "else"
            :cool      "beans"
            :awesome   1}))))

(deftest keyword->string
  (testing "Conversion of keyword into a string"
    (is "string" (kw->str "string"))
    (is "key" (kw->str :key))
    (is (thrown? IllegalArgumentException (kw->str [:x])))))

(deftest vector-cons
  (is (= (vcons :this [[:a] [:b] [:c]])
         [:this [:a] [:b] [:c]])))

(deftest make-safe-workflow-name
  (is (= (safe-workflow-name "Some \t Crazy *&(^($*!@ Workflow     Name")
         "SomeCrazyWorkflowName")
      (= (safe-workflow-name "SISTR Analyze Reads v0.2")
         "SISTRAnalyzeReadsv02")))

(deftest x-in-coll
  (is (in? [1 2 3] 2))
  (is (in? ["a" "b" "c"] "c"))
  (is (nil? (in? [1 2 3] 4)))
  (is (nil? (in? ["a" "b" "c"] "z"))))

(deftest remove-file-extension
  (is (= (rm-file-ext "filename.txt")
         "filename"))
  (is (= (rm-file-ext "genome.fasta")
         "genome"))
  (is (= (rm-file-ext "more.than.one.period.test")
         "more.than.one.period")))

(deftest get-input-name
  (testing "That we can get the tool_state name attribute of an input step"
    (is (let [g-wf (parse-ga asm-ga)
              input (input-steps g-wf)]
          (= (input-name input)
             "sequence_reads_paired")))
    (is (let [g-wf (parse-ga basic-ga)
              input (input-steps g-wf)]
          (= (input-name input)
             "Input Dataset")))
    (is (let [g-wf (parse-ga masher-ga)
              input (input-steps g-wf)]
          (= (input-name input)
             "Input Dataset Collection")))))

(deftest paired-list-or-not
  (is (let [g-wf (parse-ga asm-ga)
            input (input-steps g-wf)]
        (true? (paired? input))))
  (is (let [g-wf (parse-ga basic-ga)
            input (input-steps g-wf)]
        (false? (paired? input)))))

(deftest specifies-reference-file
  (testing "that the reference input file step is retrieved correctly"
    (is (let [wf (parse-ga snvphyl-ga)]
          (needs-reference? wf)))
    (is (let [wf (parse-ga asm-ga)]
          (not (needs-reference? wf))))))

(deftest galaxy-shed-url-has-http
  (is (false? (has-http? "irida.corefacility.ca/galaxy-shed")))
  (is (true? (has-http? "https://irida.corefacility.ca/galaxy-shed")))
  (is (false? (has-http? nil))))

(deftest tool-id-to-galaxy-shed-url
  (let [tool-id "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2"]
    (is (= (galaxy-shed-url tool-id)
           "https://toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd"))
    (is (= (galaxy-shed-url tool-id :https? false)
           "http://toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd"))))

(deftest get-galaxy-tool-changeset-revision-from-url
  (is (= (revision-from-url "https://toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd")
         "5c8ff92e38a9")))

(deftest get-tool-steps
  (testing "Getting the tool steps of a Galaxy workflow"
    (let [wf (parse-ga snvphyl-ga)
          t-steps (tool-steps wf)]
      (is (= (count t-steps) 17))
      (is (= (map get-id t-steps) (range 2 19))))
    (let [wf (parse-ga asm-ga)
          t-steps (tool-steps wf)]
      (is (= (count t-steps) 6))
      (is (= (map get-id t-steps) (range 1 7))))))
