(ns irida-wf-ga2xml.messages_test
  (:require
    [clojure.test :refer :all]
    [irida-wf-ga2xml.messages :refer :all]))

(def prokka-repo-info
  {:name     "prokka"
   :owner    "crs4"
   :revision "eaee459f3d69"
   :url      "https://toolshed.g2.bx.psu.edu"})

(def flash-repo-info
  {:name     "flash"
   :owner    "irida"
   :revision "4287dd541327"
   :url      "https://irida.corefacility.ca/galaxy-shed"})

(def biohansel-repo-info
  {:owner    "nml"
   :name     "bio_hansel"
   :revision "4654c51dae72"
   :url      "https://toolshed.g2.bx.psu.edu"})

(def prokka-expected-param-attrs
  {:label {"genus"                      "Genus name (--genus)",
           "species"                    "Species name (--species)",
           "gffver"                     "GFF version (--gffver)",
           "centre"                     "Sequencing centre ID (--centre)",
           "kingdom.kingdom_select"     "Kingdom (--kingdom)",
           "compliant.compliant_select" "Force GenBank/ENA/DDJB compliance (--compliant)",
           "increment"                  "Locus tag counter increment (--increment)",
           "evalue"                     "Similarity e-value cut-off",
           "locustag"                   "Locus tag prefix (--locustag)",
           "outputs"                    "Additional outputs",
           "kingdom.gcode"              "Genetic code (transl_table)",
           "fast"                       "Fast mode (--fast)",
           "usegenus"                   "Use genus-specific BLAST database (--usegenus)",
           "input"                      "Contigs to annotate",
           "plasmid"                    "Plasmid name or identifier (--plasmid)",
           "notrna"                     "Don't run tRNA search with Aragorn",
           "compliant.mincontig"        "Minimum contig size (--mincontiglen)",
           "proteins"                   "Optional FASTA file of trusted proteins to first annotate from (--proteins)",
           "norrna"                     "Don't run rRNA search with Barrnap",
           "metagenome"                 "Improve gene predictions for highly fragmented genomes (--metagenome)",
           "compliant.addgenes"         "Add 'gene' features for each 'CDS' feature (--addgenes)",
           "strain"                     "Strain name (--strain)",
           "rfam"                       "Enable searching for ncRNAs with Infernal+Rfam (SLOW!) (--rfam)"},
   :type  {"genus"                      "text",
           "species"                    "text",
           "gffver"                     "select",
           "centre"                     "text",
           "kingdom.kingdom_select"     "select",
           "compliant.compliant_select" "select",
           "increment"                  "integer",
           "evalue"                     "float",
           "locustag"                   "text",
           "outputs"                    "select",
           "kingdom.gcode"              "integer",
           "fast"                       "boolean",
           "usegenus"                   "boolean",
           "input"                      "data",
           "plasmid"                    "text",
           "notrna"                     "boolean",
           "compliant.mincontig"        "integer",
           "proteins"                   "data",
           "norrna"                     "boolean",
           "metagenome"                 "boolean",
           "compliant.addgenes"         "boolean",
           "strain"                     "text",
           "rfam"                       "boolean"}})

(def flash-expected-param-attrs
  {:label {"input_type.sPaired"           "Single Pair or Collection",
           "min_overlap"                  "Minimum overlap",
           "options.options_select"       "Options Type",
           "options.cap_mismatch_quals"   "Cap mismatch quality scores",
           "outputs.output_type"          "Output type",
           "options.fragment_length"      "Fragment length",
           "input_type.pInput2"           "Reverse FASTQ file",
           "options.max_mismatch_density" "Maximum mismatch density",
           "input_type.fastq_collection"  "Paired-end Fastq collection",
           "options.quiet"                "Do not print informational messages",
           "options.read_length"          "Average read length",
           "options.fragment_stdev"       "Fragment length standard deviation",
           "input_type.pInput1"           "Forward FASTQ file",
           "max_overlap"                  "Maximum overlap",
           "options.phred_offset"         "Phred-offset"},
   :type  {"input_type.sPaired"           "select",
           "min_overlap"                  "integer",
           "options.options_select"       "select",
           "options.cap_mismatch_quals"   "boolean",
           "outputs.output_type"          "select",
           "options.fragment_length"      "integer",
           "input_type.pInput2"           "data",
           "options.max_mismatch_density" "float",
           "input_type.fastq_collection"  "data_collection",
           "options.quiet"                "boolean",
           "options.read_length"          "integer",
           "options.fragment_stdev"       "integer",
           "input_type.pInput1"           "data",
           "max_overlap"                  "integer",
           "options.phred_offset"         "select"}})

(def biohansel-expected-param-attrs
  {:label {"input.reverse"                  "Reverse FASTQ file",
           "qc_vals.max_missing_tiles"      "QC: Decimal Proportion of max allowed missing tiles",
           "kmer_vals.kmer_max"             "Max k-mer frequency/coverage",
           "type_of_scheme.scheme_type"     "Specify scheme to use. (Heidelberg is default)",
           "input.paired_collection"        "Paired-end FASTQ collection",
           "input.single"                   "Single-end FASTQ file",
           "qc_vals.max_intermediate_tiles" "QC: Decimal Proportion of max allowed missing tiles for an intermediate subtype",
           "qc_vals.low_coverage_warning"   "QC: Overall tile coverage below this value will trigger a low coverage warning",
           "dev_args.use_json"              "Output JSON results",
           "input.fasta"                    "FASTA file",
           "qc_vals.min_ambiguous_tiles"    "QC: Min number of tiles missing for Ambiguous Result",
           "input.type"                     "Sequence input type",
           "qc_vals.low_cov_depth_freq"     "QC: Frequency below this coverage are considered low coverage",
           "type_of_scheme.scheme_input"    "Scheme Input",
           "input.forward"                  "Forward FASTQ file",
           "kmer_vals.kmer_min"             "Min k-mer frequency/coverage"},
   :type  {"input.reverse"                  "data",
           "qc_vals.max_missing_tiles"      "float",
           "kmer_vals.kmer_max"             "integer",
           "type_of_scheme.scheme_type"     "select",
           "input.paired_collection"        "data_collection",
           "input.single"                   "data",
           "qc_vals.max_intermediate_tiles" "float",
           "qc_vals.low_coverage_warning"   "integer",
           "dev_args.use_json"              "boolean",
           "input.fasta"                    "data",
           "qc_vals.min_ambiguous_tiles"    "integer",
           "input.type"                     "select",
           "qc_vals.low_cov_depth_freq"     "integer",
           "type_of_scheme.scheme_input"    "data",
           "input.forward"                  "data",
           "kmer_vals.kmer_min"             "integer"}})

(deftest param-values-map-from-tool-repo-info
  (is (= prokka-expected-param-attrs
         (repo-info->param-attr-map prokka-repo-info)))
  (is (= flash-expected-param-attrs
         (repo-info->param-attr-map flash-repo-info)))
  (is (= biohansel-expected-param-attrs
         (repo-info->param-attr-map biohansel-repo-info))))

(deftest build-messages-map
  (let [default-msgs (default-workflow-messages "AssemblyAnnotation" "asm_annt" "Assembles and Annotates")]
    (is (= {"workflow.asm_annt.title"                            "AssemblyAnnotation Pipeline",
            "workflow.asm_annt.description"                      "Assembles and Annotates",
            "workflow.label.share-analysis-samples.asm_annt"     "Save Results to Project Line List Metadata",
            "pipeline.title.AssemblyAnnotation"                  "Pipelines - AssemblyAnnotation",
            "pipeline.h1.AssemblyAnnotation"                     "AssemblyAnnotation Pipeline",
            "pipeline.parameters.modal-title.assemblyannotation" "AssemblyAnnotation Pipeline Parameters"}
           default-msgs))))

(deftest tool-step-info-from-messages-key
  (let [some-msg-key "pipeline.parameters.biohansel.bio_hansel-1-type_of_scheme.scheme_type"]
    (is (msg-key->tool-step-number-map some-msg-key)
        {:tool "bio_hansel", :step-number "1"})))

(deftest construct-tool-param-properties-keys
  (testing "The workflow name should be lowercase in the properties file tool param key names"
    (let [wf-name "AssemblyAnnotation"
          params {"prokka-6-evalue"                       "Similarity e-value cut-off"
                  "filter_spades_repeats-4-print_summary" "Print out a summary of all the results?"
                  "flash-1-max_overlap"                   "Maximum overlap",
                  "spades-2-kmer_choice.auto_kmer_choice" "Automatically choose k-mer values"}
          expected {"pipeline.parameters.assemblyannotation.prokka-6-evalue"                       "Similarity e-value cut-off"
                    "pipeline.parameters.assemblyannotation.filter_spades_repeats-4-print_summary" "Print out a summary of all the results?"
                    "pipeline.parameters.assemblyannotation.flash-1-max_overlap"                   "Maximum overlap",
                    "pipeline.parameters.assemblyannotation.spades-2-kmer_choice.auto_kmer_choice" "Automatically choose k-mer values"}]
      (is (= expected
             (prepend-param-details wf-name params))))))


(deftest finding-param-props
  (testing "That a list of param keys can be found in a map of param keys to param values"
    (let [params ["a" "b" "missing" "c"]
          props {"a" "x"
                 "b" "y"
                 "c" "z"}
          expected {"a"       "x"
                    "b"       "y"
                    "missing" "missing"
                    "c"       "z"}]
      (is (= expected
             (find-param-props params props))))
    (let [params ["a" "b" "c"]
          props {"a" "x"
                 "b" "y"
                 "c" "z"}
          expected {"a" "x"
                    "b" "y"
                    "c" "z"}]
      (is (= expected
             (find-param-props params props))))
    (let [params ["a" "b" "c"]
          props {}
          expected {"a" "a"
                    "b" "b"
                    "c" "c"}]
      (is (= expected
             (find-param-props params props))))))

(deftest construction-of-tool-param-props-map
  (testing "That the tool param props map is constructed properly given a Galaxy tool XML attributes map, tool id and workflow step number"
    (let [tool-id "prokka"
          step-number 99
          expected {"prokka-99-genus"                      "Genus name (--genus)",
                    "prokka-99-species"                    "Species name (--species)",
                    "prokka-99-gffver"                     "GFF version (--gffver)",
                    "prokka-99-centre"                     "Sequencing centre ID (--centre)",
                    "prokka-99-kingdom.kingdom_select"     "Kingdom (--kingdom)",
                    "prokka-99-compliant.compliant_select" "Force GenBank/ENA/DDJB compliance (--compliant)",
                    "prokka-99-increment"                  "Locus tag counter increment (--increment)",
                    "prokka-99-evalue"                     "Similarity e-value cut-off",
                    "prokka-99-locustag"                   "Locus tag prefix (--locustag)",
                    "prokka-99-outputs"                    "Additional outputs",
                    "prokka-99-kingdom.gcode"              "Genetic code (transl_table)",
                    "prokka-99-fast"                       "Fast mode (--fast)",
                    "prokka-99-usegenus"                   "Use genus-specific BLAST database (--usegenus)",
                    "prokka-99-input"                      "Contigs to annotate",
                    "prokka-99-plasmid"                    "Plasmid name or identifier (--plasmid)",
                    "prokka-99-notrna"                     "Don't run tRNA search with Aragorn",
                    "prokka-99-compliant.mincontig"        "Minimum contig size (--mincontiglen)",
                    "prokka-99-proteins"                   "Optional FASTA file of trusted proteins to first annotate from (--proteins)",
                    "prokka-99-norrna"                     "Don't run rRNA search with Barrnap",
                    "prokka-99-metagenome"                 "Improve gene predictions for highly fragmented genomes (--metagenome)",
                    "prokka-99-compliant.addgenes"         "Add 'gene' features for each 'CDS' feature (--addgenes)",
                    "prokka-99-strain"                     "Strain name (--strain)",
                    "prokka-99-rfam"                       "Enable searching for ncRNAs with Infernal+Rfam (SLOW!) (--rfam)"}]
      (is (= expected
             (tool-param-props tool-id step-number prokka-expected-param-attrs))))))
