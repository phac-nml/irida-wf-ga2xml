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

(def prokka-expected-param-label-map
  {"genus"                      "Genus name (--genus)",
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
   "rfam"                       "Enable searching for ncRNAs with Infernal+Rfam (SLOW!) (--rfam)"})


(def prokka-expected-param-type-map
  {"genus"                      "text",
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
   "rfam"                       "boolean"})



(def flash-expected-param-label-map
  {"input_type.sPaired"           "Single Pair or Collection",
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
   "options.phred_offset"         "Phred-offset"})

(def biohansel-expected-param-label-map
  {"input.reverse"                  "Reverse FASTQ file",
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
   "kmer_vals.kmer_min"             "Min k-mer frequency/coverage"})

(deftest param-values-map-from-tool-repo-info
  (is (= prokka-expected-param-label-map
         (repo-info->param-attr-map prokka-repo-info)))
  (is (= prokka-expected-param-type-map
         (repo-info->param-attr-map prokka-repo-info :get-attr :type)))
  (is (= flash-expected-param-label-map
         (repo-info->param-attr-map flash-repo-info)))
  (is (= biohansel-expected-param-label-map
         (repo-info->param-attr-map biohansel-repo-info))))

(deftest build-messages-map
  (let [default-msgs (default-workflow-messages "biohansel" "biohansel" "Hansel?")]
    (is (= {"workflow.biohansel.title" "biohansel Pipeline",
            "workflow.biohansel.description" "Hansel?",
            "pipeline.title.biohansel" "Pipelines - biohansel",
            "pipeline.h1.biohansel" "biohansel Pipeline",
            "pipeline.parameters.modal-title.biohansel" "biohansel Pipeline Parameters"}
           default-msgs))
    ))
