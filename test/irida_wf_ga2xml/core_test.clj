(ns irida-wf-ga2xml.core-test
  (:require [clojure.test :refer :all]
            [irida-wf-ga2xml.core :refer [tool-id->repo-info
                                          tool-repo
                                          get-step-outputs
                                          tool-params-vec
                                          to-wf-vec]]
            [irida-wf-ga2xml.util :refer [vec->indented-xml
                                          tool-steps
                                          parse-ga]]))

(def basic-wf-ga "test/data/basic-sistr-fasta-workflow.ga")
(def sistr-ga "test/data/irida_workflow_structure.ga")
(def snvphyl-ga "test/data/snvphyl-1.0.1-workflow.ga")

(def sistr-flash-step {"tool_errors"       nil,
                       "input_connections" {"input_type|fastq_collection" {"id" 0, "output_name" "output"}},
                       "label"             nil,
                       "id"                1,
                       "tool_state"        "{\"__page__\": 0, \"min_overlap\": \"\\\"20\\\"\", \"input_type\": \"{\\\"fastq_collection\\\": null, \\\"sPaired\\\": \\\"collections\\\", \\\"__current_case__\\\": 1}\", \"__rerun_remap_job_id__\": null, \"max_overlap\": \"\\\"300\\\"\", \"options\": \"{\\\"__current_case__\\\": 1, \\\"options_select\\\": \\\"basic\\\"}\", \"outputs\": \"{\\\"output_type\\\": \\\"Non-interleaved_fastq\\\", \\\"__current_case__\\\": 0}\"}",
                       "position"          {"left" 447, "top" 200},
                       "name"              "FLASH",
                       "uuid"              "d1d70bc2-41f1-43d3-983d-c1d66f5b8470",
                       "outputs"           [{"name" "extendedFrags", "type" "fastqsanger"}
                                            {"name" "notCombined1", "type" "fastqsanger"}
                                            {"name" "notCombined2", "type" "fastqsanger"}
                                            {"name" "interNotCombined", "type" "fastqsanger"}
                                            {"name" "readsAndPairs", "type" "tabular"}
                                            {"name" "log_file", "type" "txt"}],
                       "type"              "tool",
                       "tool_version"      "1.3.0",
                       "user_outputs"      [],
                       "annotation"        "",
                       "inputs"            [],
                       "post_job_actions"  {"HideDatasetActionextendedFrags"    {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "extendedFrags"},
                                            "HideDatasetActioninterNotCombined" {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "interNotCombined"},
                                            "HideDatasetActionlog_file"         {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "log_file"},
                                            "HideDatasetActionnotCombined1"     {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "notCombined1"},
                                            "HideDatasetActionnotCombined2"     {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "notCombined2"},
                                            "HideDatasetActionreadsAndPairs"    {"action_arguments" {},
                                                                                 "action_type"      "HideDatasetAction",
                                                                                 "output_name"      "readsAndPairs"},
                                            "RenameDatasetActionlog_file"       {"action_arguments" {"newname" "flash.log"},
                                                                                 "action_type"      "RenameDatasetAction",
                                                                                 "output_name"      "log_file"}},
                       "tool_id"           "irida.corefacility.ca/galaxy-shed/repos/irida/flash/FLASH/1.3.0"})

(def sistr-flash-step-repo-xml "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<repository>\n  <name>flash</name>\n  <owner>irida</owner>\n  <url>https://irida.corefacility.ca/galaxy-shed</url>\n  <revision>4287dd541327</revision>\n  <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/irida/flash-->\n</repository>\n")

(def sistr-cmd-step {"tool_errors"       nil,
                     "input_connections" {"input_fastas" {"id" 4, "output_name" "output_with_repeats"}},
                     "label"             nil,
                     "id"                6,
                     "tool_state"        "{\"input_fastas\": \"null\", \"no_cgmlst\": \"\\\"False\\\"\", \"use_full_cgmlst_db\": \"\\\"True\\\"\", \"__page__\": 0, \"output_format\": \"\\\"json\\\"\", \"keep_tmp\": \"\\\"False\\\"\", \"run_mash\": \"\\\"True\\\"\", \"more_output\": \"\\\"-M\\\"\", \"__rerun_remap_job_id__\": null, \"qc\": \"\\\"True\\\"\", \"verbosity\": \"\\\"-vv\\\"\"}",
                     "position"          {"left" 1654, "top" 490},
                     "name"              "sistr_cmd",
                     "uuid"              "3aa85dac-c737-44fc-8f97-8f7ed6cd798d",
                     "outputs"           [{"name" "output_prediction_csv", "type" "csv"}
                                          {"name" "output_prediction_json", "type" "json"}
                                          {"name" "output_prediction_tab", "type" "tabular"}
                                          {"name" "cgmlst_profiles", "type" "csv"}
                                          {"name" "novel_alleles", "type" "fasta"}
                                          {"name" "alleles_output", "type" "json"}],
                     "type"              "tool",
                     "tool_version"      "1.0.2",
                     "user_outputs"      [],
                     "annotation"        "",
                     "inputs"            [],
                     "post_job_actions"  {"HideDatasetActionoutput_prediction_csv"    {"action_arguments" {},
                                                                                       "action_type"      "HideDatasetAction",
                                                                                       "output_name"      "output_prediction_csv"},
                                          "HideDatasetActionoutput_prediction_tab"    {"action_arguments" {},
                                                                                       "action_type"      "HideDatasetAction",
                                                                                       "output_name"      "output_prediction_tab"},
                                          "RenameDatasetActionalleles_output"         {"action_arguments" {"newname" "sistr-alleles-out.json"},
                                                                                       "action_type"      "RenameDatasetAction",
                                                                                       "output_name"      "alleles_output"},
                                          "RenameDatasetActioncgmlst_profiles"        {"action_arguments" {"newname" "sistr-cgmlst-profiles.csv"},
                                                                                       "action_type"      "RenameDatasetAction",
                                                                                       "output_name"      "cgmlst_profiles"},
                                          "RenameDatasetActionnovel_alleles"          {"action_arguments" {"newname" "sistr-novel-alleles.fasta"},
                                                                                       "action_type"      "RenameDatasetAction",
                                                                                       "output_name"      "novel_alleles"},
                                          "RenameDatasetActionoutput_prediction_json" {"action_arguments" {"newname" "sistr-predictions.json"},
                                                                                       "action_type"      "RenameDatasetAction",
                                                                                       "output_name"      "output_prediction_json"}},
                     "tool_id"           "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2"})

(def sistr-cmd-step-new {"tool_errors"          nil,
                         "workflow_outputs"     [{"label"       nil,
                                                  "output_name" "alleles_output",
                                                  "uuid"        "93f63a3b-ef41-4cd0-b378-c7ceaf624959"}
                                                 {"label"       nil,
                                                  "output_name" "output_prediction_json",
                                                  "uuid"        "484986a3-68cb-4e75-afaa-cb7cd196a85e"}
                                                 {"label"       nil,
                                                  "output_name" "novel_alleles",
                                                  "uuid"        "a6a77064-95bf-4e35-bf27-4e6434d3fd20"}
                                                 {"label"       nil,
                                                  "output_name" "cgmlst_profiles",
                                                  "uuid"        "9cbd838e-8331-4969-9824-6b5c16a8e279"}],
                         "input_connections"    {"input_fastas" {"id" 0, "output_name" "output"}},
                         "label"                nil,
                         "id"                   1,
                         "tool_state"           "{\"input_fastas\": \"{\\\"__class__\\\": \\\"RuntimeValue\\\"}\", \"no_cgmlst\": \"\\\"false\\\"\", \"use_full_cgmlst_db\": \"\\\"false\\\"\", \"__page__\": 0, \"output_format\": \"\\\"json\\\"\", \"keep_tmp\": \"\\\"false\\\"\", \"run_mash\": \"\\\"true\\\"\", \"more_output\": \"\\\"-MM\\\"\", \"__rerun_remap_job_id__\": null, \"qc\": \"\\\"true\\\"\", \"verbosity\": \"\\\"-vv\\\"\"}",
                         "position"             {"left" 428.015625, "top" 199.9921875},
                         "name"                 "sistr_cmd",
                         "uuid"                 "7aa0fb20-be4c-4821-a60c-ba1e13a02ec9",
                         "outputs"              [{"name" "output_prediction_csv", "type" "csv"}
                                                 {"name" "output_prediction_json", "type" "json"}
                                                 {"name" "output_prediction_tab", "type" "tabular"}
                                                 {"name" "cgmlst_profiles", "type" "csv"}
                                                 {"name" "novel_alleles", "type" "fasta"}
                                                 {"name" "alleles_output", "type" "json"}],
                         "tool_shed_repository" {"changeset_revision" "5c8ff92e38a9",
                                                 "name"               "sistr_cmd",
                                                 "owner"              "nml",
                                                 "tool_shed"          "toolshed.g2.bx.psu.edu"},
                         "type"                 "tool",
                         "tool_version"         "1.0.2",
                         "annotation"           "",
                         "inputs"               [{"description" "runtime parameter for tool sistr_cmd", "name" "input_fastas"}],
                         "post_job_actions"     {"HideDatasetActionoutput_prediction_csv"    {"action_arguments" {},
                                                                                              "action_type"      "HideDatasetAction",
                                                                                              "output_name"      "output_prediction_csv"},
                                                 "HideDatasetActionoutput_prediction_tab"    {"action_arguments" {},
                                                                                              "action_type"      "HideDatasetAction",
                                                                                              "output_name"      "output_prediction_tab"},
                                                 "RenameDatasetActionalleles_output"         {"action_arguments" {"newname" "sistr-alleles.json"},
                                                                                              "action_type"      "RenameDatasetAction",
                                                                                              "output_name"      "alleles_output"},
                                                 "RenameDatasetActioncgmlst_profiles"        {"action_arguments" {"newname" "sistr-cgmlst-profiles.csv"},
                                                                                              "action_type"      "RenameDatasetAction",
                                                                                              "output_name"      "cgmlst_profiles"},
                                                 "RenameDatasetActionnovel_alleles"          {"action_arguments" {"newname" "sistr-novel-alleles.fasta"},
                                                                                              "action_type"      "RenameDatasetAction",
                                                                                              "output_name"      "novel_alleles"},
                                                 "RenameDatasetActionoutput_prediction_json" {"action_arguments" {"newname" "sistr-predictions.json"},
                                                                                              "action_type"      "RenameDatasetAction",
                                                                                              "output_name"      "output_prediction_json"}},
                         "content_id"           "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2",
                         "tool_id"              "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2"})

(def sistr-cmd-step-new-tool-params
  [[:parameter
    {:name "sistr_cmd-1-keep_tmp", :defaultValue "false"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "keep_tmp"}]]
   [:parameter
    {:name "sistr_cmd-1-output_format", :defaultValue "json"}
    [:toolParameter
     {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "output_format"}]]
   [:parameter
    {:name "sistr_cmd-1-run_mash", :defaultValue "true"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "run_mash"}]]
   [:parameter
    {:name "sistr_cmd-1-more_output", :defaultValue "-MM"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "more_output"}]]
   [:parameter
    {:name "sistr_cmd-1-verbosity", :defaultValue "-vv"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "verbosity"}]]
   [:parameter
    {:name "sistr_cmd-1-use_full_cgmlst_db", :defaultValue "false"}
    [:toolParameter
     {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "use_full_cgmlst_db"}]]
   [:parameter
    {:name "sistr_cmd-1-qc", :defaultValue "true"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "qc"}]]
   [:parameter
    {:name "sistr_cmd-1-no_cgmlst", :defaultValue "false"}
    [:toolParameter {:toolId "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2", :parameterName "no_cgmlst"}]]])

(def snvphyl-tool-steps (tool-steps (parse-ga snvphyl-ga)))

(def snvphyl-freebayes-step (first (get (group-by #(get % "id") snvphyl-tool-steps) 6)))
(def snvphyl-cat-step (first (get (group-by #(get % "id") snvphyl-tool-steps) 13)))

(def snvphyl-freebayes-step-tool-params
  [[:parameter
    {:name "freebayes-6-options_type.algorithmic_features.algorithmic_features_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.algorithmic_features.algorithmic_features_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_mappability_priors.population_mappability_priors_selector",
     :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_mappability_priors.population_mappability_priors_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.reference_allele.reference_allele_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.reference_allele.reference_allele_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_model.T", :defaultValue "0.001"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_model.T"}]]
   [:parameter
    {:name "freebayes-6-options_type.input_filters.input_filters_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.input_filters.input_filters_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_model.population_model_selector", :defaultValue "True"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_model.population_model_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.optional_inputs.optional_inputs_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.optional_inputs.optional_inputs_selector"}]]
   [:parameter
    {:name "freebayes-6-reference_source.reference_source_selector", :defaultValue "history"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "reference_source.reference_source_selector"}]]
   [:parameter
    {:name "freebayes-6-target_limit_type.target_limit_type_selector", :defaultValue "do_not_limit"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "target_limit_type.target_limit_type_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_model.J", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_model.J"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_model.K", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_model.K"}]]
   [:parameter
    {:name "freebayes-6-options_type.genotype_likelihoods.genotype_likelihoods_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.genotype_likelihoods.genotype_likelihoods_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.O", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.O"}]]
   [:parameter
    {:name "freebayes-6-options_type.options_type_selector", :defaultValue "full"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.options_type_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.reporting.reporting_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.reporting.reporting_selector"}]]
   [:parameter
    {:name "freebayes-6-options_type.population_model.P", :defaultValue "1"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.population_model.P"}]]
   [:parameter
    {:name "freebayes-6-options_type.allele_scope.allele_scope_selector", :defaultValue "False"}
    [:toolParameter
     {:toolId "irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1",
      :parameterName "options_type.allele_scope.allele_scope_selector"}]]])

(deftest tool-shed-repo-info-from-tool-id
  (testing "Parsing of tool shed url, owner, tool name from tool id"
    (is (= (tool-id->repo-info "toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd/sistr_cmd/1.0.2")
           {:url "toolshed.g2.bx.psu.edu", :owner "nml", :name "sistr_cmd"}))))

(deftest outputs-from-workflow-step
  (testing "Getting outputs for FLASH step in SISTR from reads workflow"
    (is (= (vec (get-step-outputs sistr-flash-step))
           [[:output {:name "log_file", :fileName "flash.log"}]])))
  (testing "Getting outputs for sistr_cmd step"
    (is (= (vec (get-step-outputs sistr-cmd-step))
           [[:output {:name "alleles_output", :fileName "sistr-alleles-out.json"}]
            [:output {:name "cgmlst_profiles", :fileName "sistr-cgmlst-profiles.csv"}]
            [:output {:name "novel_alleles", :fileName "sistr-novel-alleles.fasta"}]
            [:output {:name "output_prediction_json", :fileName "sistr-predictions.json"}]]))))

(deftest parse-tool-shed-repo-info
  (testing "Parsing of Galaxy workflow step for tool toolshed repository information"
    (is (= (tool-repo sistr-cmd-step-new)
           [:repository [:name "sistr_cmd"] [:owner "nml"] [:url "https://toolshed.g2.bx.psu.edu"] [:revision "5c8ff92e38a9"]]))
    (is (= (tool-repo sistr-cmd-step)
           [:repository
            [:name "sistr_cmd"]
            [:owner "nml"]
            [:url "https://toolshed.g2.bx.psu.edu"]
            [:revision "5c8ff92e38a9"]
            [:-comment "WARNING: Latest revision fetched from https://toolshed.g2.bx.psu.edu/repos/nml/sistr_cmd"]]))
    (is (nil? (tool-repo snvphyl-cat-step)))))

(deftest parse-tool-params
  (testing "Parsing of sistr_cmd tool parameters."
    (is (= (tool-params-vec sistr-cmd-step-new)
           sistr-cmd-step-new-tool-params)))
  (testing "Parsing of SNVPhyl FreeBayes step deeply nested parameters."
    (is (= (tool-params-vec snvphyl-freebayes-step)
           snvphyl-freebayes-step-tool-params))))

(deftest workflow-to-vector
  (testing "Parsing of basic sistr_cmd workflow with FASTA input"
    (let [wf (to-wf-vec basic-wf-ga)]
      (is (= (first wf)
             :iridaWorkflow))
      (is (= (count wf)
             9))))
  (testing "Parsing of basic sistr_cmd workflow with FASTQ input"
    (let [wf (to-wf-vec sistr-ga)]
      (is (= (first wf)
             :iridaWorkflow))
      (is (= (count wf)
             9))))
  (testing "Parsing of SNVPhyl workflow"
    (let [wf (to-wf-vec snvphyl-ga
                        :single-sample? false
                        :wf-version "1.0.1"
                        :analysis-type "phylogenomics")]
      (is (= (first wf)
             :iridaWorkflow))
      (is (= (count wf)
             9)))))

(deftest vec-to-xml
  (let [flash-shed-info (tool-repo sistr-flash-step)]
    (is (= (vec->indented-xml flash-shed-info)
           sistr-flash-step-repo-xml))))
