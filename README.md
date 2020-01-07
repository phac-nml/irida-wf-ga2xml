# irida-wf-ga2xml

[![Build Status](https://travis-ci.org/phac-nml/irida-wf-ga2xml.svg?branch=master)](https://travis-ci.org/phac-nml/irida-wf-ga2xml)

Create an IRIDA workflow from a Galaxy workflow file.

This JVM application creates a XML workflow description (`irida_structure.xml`) and messages file (`messages_en.properties`) from a Galaxy workflow `*.ga` JSON file.

## Installation

- Java 1.8 must be installed 
- Download [latest standalone release of `irida-wf-ga2xml`](https://github.com/phac-nml/irida-wf-ga2xml/releases)

## Usage

Execute standalone JAR with `java`:

    $ java -jar irida-wf-ga2xml-1.0.0-SNAPSHOT-standalone.jar -h

    irida-wf-ga2xml v1.0.0: Output an IRIDA workflow directory with irida_structure.xml, messages_en.properties and irida_workflow_structure.ga files from a Galaxy Workflow *.ga JSON file

    Usage: irida-wf-ga2xml [options]

    Options:
      -n, --workflow-name WORKFLOW_NAME                 Workflow name (default is to extract name from workflow input file)
      -t, --analysis-type ANALYSIS_TYPE        DEFAULT  IRIDA AnalysisType
      -W, --workflow-version WORKFLOW_VERSION  0.1.0    Workflow version
      -I, --workflow-id WORKFLOW_ID                     Workflow ID (UUID)
      -o, --outdir OUPUT_DIRECTORY                      Output directory; where to create the <workflow-name>/<workflow-version>/ directory structure and write the 'irida_workflow.xml', 'irida_workflow_structure.ga' and 'messages_en.properties' files
      -m, --multi-sample                                Multiple sample workflow; not a single sample workflow
      -i, --input INPUT                                 Galaxy workflow ga JSON format file
      -x, --extra-tool-param-attrs                      Save extra toolParameter attributes ["label", "type"] to XML
          --remove-output-name-file-ext                 Remove file extension in workflow output names?
      -v, --verbosity                                   Verbosity level
      -V, --version                                     Display version
      -h, --help


## Options

- `-n`/`--workflow-name`: Workflow name to use in IRIDA workflow XML
- `-t`/`--analysis-type`: IRIDA AnalysisType enum (e.g. "phylogenomics")
- `-W`/`--workflow-version`: Galaxy workflow version number (e.g. "0.1.0")
- `-I`/`--workflow-id`: Galaxy workflow id (UUID, e.g. "ece298c1-cd9d-4aad-a6ce-a366bd5cbb9a")
- `-m`: Flag for whether the workflow operates on multiple samples (e.g. SNVPhyl does use multiple samples to generate a phylogenetic tree; SISTR operates on single samples to produce an individual result for each sample)
- `-i`/`--input`: Galaxy workflow specifiction file (e.g. `workflow.ga`); JSON format expected
- `-o`/`--outdir`: Output directory; where to create the `<workflow-name>/<workflow-version>/` directory structure and write the `irida_workflow.xml`, `irida_workflow_structure.ga` and `messages_en.properties` files
- `-x`/`--extra-tool-param-attrs`: Save extra toolParameter attributes `["label", "type"]` to XML `toolParameter` tags
- `--remove-output-name-file-ext`: Remove file extension in workflow output names? (default is to keep the extensions in the `<output>` tag names)


## Examples

Command-line for creating SNVPhyl v1.0.1 workflow files:

    $ java -jar irida-wf-ga2xml-1.0.0-SNAPSHOT-standalone.jar \
        -t phylogenomics \ # analysis type
        -W 1.0.1 \ # workflow version
        -I 'ece298c1-cd9d-4aad-a6ce-a366bd5cbb9a' \ # workflow id
        -m \ # multi-sample workflow flag
        -x \ # save label and type info to toolParameter tags
        -i test/data/snvphyl-1.0.1-workflow.ga \ # see https://github.com/phac-nml/irida-wf-ga2xml/blob/master/test/data/snvphyl-1.0.1-workflow.ga
        -n SNVPhyl # workflow name
        -o /tmp # output directory

**Log output:**

    18-08-14 21:46:55 pk INFO [irida-wf-ga2xml.main:102] - Parsing  test/data/snvphyl-1.0.1-workflow.ga  Galaxy workflow file. Creating irida_workflow.xml with workflow name ' SNVPhyl ' and version ' 1.0.1 '.
    18-08-14 21:46:55 pk INFO [irida-wf-ga2xml.core:119] - Parsed Galaxy workflow file with name='SNVPhyl v1.0.1 Paired-End' and annotation/description='snvphyl'
    18-08-14 21:46:55 pk INFO [irida-wf-ga2xml.core:121] - Using worklfow name='SNVPhyl'
    18-08-14 21:46:56 pk INFO [irida-wf-ga2xml.core:123] - 16 input steps in workflow
    18-08-14 21:46:56 pk INFO [irida-wf-ga2xml.core:125] - 17 tool execution steps in workflow
    18-08-14 21:47:03 pk ERROR [irida-wf-ga2xml.messages:174] - Could not get tool parameter attribute info for {} ; Encountered error: java.io.FileNotFoundException: /repos/file (No such file or directory)
    18-08-14 21:47:07 pk INFO [irida-wf-ga2xml.main:119] - Wrote workflow XML to  /tmp/SNVPhyl/1.0.1/irida_workflow.xml
    18-08-14 21:47:07 pk INFO [irida-wf-ga2xml.main:121] - Wrote Galaxy workflow *.ga file to  /tmp/SNVPhyl/1.0.1/irida_workflow_structure.ga
    18-08-14 21:47:07 pk INFO [irida-wf-ga2xml.main:128] - Wrote IRIDA messages to  /tmp/SNVPhyl/1.0.1/messages_en.properties

*Note: The `Could not get tool parameter attribute info for {}` error in the log can be safely ignored and indicates that the Galaxy tool XML for a particular tool could not be found.*


**Output File Tree:**

    SNVPhyl/
    `-- 1.0.1
        |-- irida_workflow_structure.ga
        |-- irida_workflow.xml
        `-- messages_en.properties

    1 directory, 3 files


*Note: You may need to curate which tool parameters you want to keep in the `irida_workflow.xml` and `messages_en.properties` files as `irida-wf-ga2xml` will get all of them from the Galaxy workflow file.*

**Contents of `messages_en.properties`:**

*Note: You may need to update some of the messages in the `messages_en.properties` file.*

```properties
#Pipeline Info Properties
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.modal-title.snvphyl=SNVPhyl Pipeline Parameters
pipeline.title.SNVPhyl=Pipelines - SNVPhyl
pipeline.h1.SNVPhyl=SNVPhyl Pipeline
workflow.phylogenomics.description=snvphyl
workflow.phylogenomics.title=SNVPhyl Pipeline
#Tool Parameters - Tool: smalt_index - Workflow Step #: 2
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.smalt_index-2-s=Step size
pipeline.parameters.snvphyl.smalt_index-2-k=K-mer size
#Tool Parameters - Tool: find_repeats - Workflow Step #: 3
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.find_repeats-3-length=find_repeats-3-length
pipeline.parameters.snvphyl.find_repeats-3-pid=find_repeats-3-pid
#Tool Parameters - Tool: smalt_map - Workflow Step #: 4
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.smalt_map-4-scordiff=Scordiff
pipeline.parameters.snvphyl.smalt_map-4-oformat.outformat=Format
pipeline.parameters.snvphyl.smalt_map-4-singlePaired.sPaired=What is the library type?
pipeline.parameters.snvphyl.smalt_map-4-mincover=Mincover
pipeline.parameters.snvphyl.smalt_map-4-minscor=Sets an absolute threshold of the Smith-Waterman scores.
pipeline.parameters.snvphyl.smalt_map-4-minid=Sets an identity threshold for a mapping to be reported (default\: 0).
pipeline.parameters.snvphyl.smalt_map-4-insertmin=Minimum insert size (only in paired-end mode). 
pipeline.parameters.snvphyl.smalt_map-4-singlePaired.pairtype=Pair Type
pipeline.parameters.snvphyl.smalt_map-4-insertmax=Maximum insert size (only in paired-end mode). 
pipeline.parameters.snvphyl.smalt_map-4-minbasq=Sets a base quality threshold (0 <\= minbasq <\= 10, default 0)
pipeline.parameters.snvphyl.smalt_map-4-seed=If the there are multiple mappings with the same best alignment score report one picked at random.
pipeline.parameters.snvphyl.smalt_map-4-sw_weighted=Smith-Waterman scores are complexity weighted.
pipeline.parameters.snvphyl.smalt_map-4-search_harder=This flag triggers a more exhaustive search for alignments at the cost of decreased speed
#Tool Parameters - Tool: verify_map - Workflow Step #: 5
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.verify_map-5-mindepth=verify_map-5-mindepth
pipeline.parameters.snvphyl.verify_map-5-minmap=verify_map-5-minmap
#Tool Parameters - Tool: freebayes - Workflow Step #: 6
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.freebayes-6-reference_source.reference_source_selector=Load reference genome from
pipeline.parameters.snvphyl.freebayes-6-options_type.reference_allele.reference_allele_selector=Use reference allele?
pipeline.parameters.snvphyl.freebayes-6-options_type.algorithmic_features.algorithmic_features_selector=Tweak algorithmic features?
pipeline.parameters.snvphyl.freebayes-6-options_type.allele_scope.allele_scope_selector=Set allelic scope?
pipeline.parameters.snvphyl.freebayes-6-options_type.options_type_selector=Choose parameter selection level
pipeline.parameters.snvphyl.freebayes-6-options_type.reporting.reporting_selector=Set reporting option?
pipeline.parameters.snvphyl.freebayes-6-options_type.O=Turn off left-alignment of indels?
pipeline.parameters.snvphyl.freebayes-6-options_type.population_model.population_model_selector=Set population model?
pipeline.parameters.snvphyl.freebayes-6-options_type.optional_inputs.optional_inputs_selector=Do you want to provide additional inputs?
pipeline.parameters.snvphyl.freebayes-6-options_type.population_model.T=The expected mutation rate or pairwise nucleotide diversity among the population under analysis
pipeline.parameters.snvphyl.freebayes-6-options_type.input_filters.input_filters_selector=Set input filters?
pipeline.parameters.snvphyl.freebayes-6-target_limit_type.target_limit_type_selector=Limit variant calling to a set of regions?
pipeline.parameters.snvphyl.freebayes-6-options_type.genotype_likelihoods.genotype_likelihoods_selector=Tweak genotype likelihoods?
pipeline.parameters.snvphyl.freebayes-6-options_type.population_model.P=Set ploidy for the analysis
pipeline.parameters.snvphyl.freebayes-6-options_type.population_mappability_priors.population_mappability_priors_selector=Set population and mappability priors?
pipeline.parameters.snvphyl.freebayes-6-options_type.population_model.K=Output all alleles which pass input filters, regardles of genotyping outcome or model
pipeline.parameters.snvphyl.freebayes-6-options_type.population_model.J=Assume that samples result from pooled sequencing
#Tool Parameters - Tool: samtools_mpileup - Workflow Step #: 7
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.samtools_mpileup-7-reference_source.reference_source_selector=samtools_mpileup-7-reference_source.reference_source_selector
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.disable_probabilistic_realignment=samtools_mpileup-7-advanced_options.disable_probabilistic_realignment
pipeline.parameters.snvphyl.samtools_mpileup-7-genotype_likelihood_computation_type.genotype_likelihood_computation_type_selector=samtools_mpileup-7-genotype_likelihood_computation_type.genotype_likelihood_computation_type_selector
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.minimum_base_quality=samtools_mpileup-7-advanced_options.minimum_base_quality
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.coefficient_for_downgrading=samtools_mpileup-7-advanced_options.coefficient_for_downgrading
pipeline.parameters.snvphyl.samtools_mpileup-7-genotype_likelihood_computation_type.output_format=samtools_mpileup-7-genotype_likelihood_computation_type.output_format
pipeline.parameters.snvphyl.samtools_mpileup-7-genotype_likelihood_computation_type.perform_indel_calling.perform_indel_calling_selector=samtools_mpileup-7-genotype_likelihood_computation_type.perform_indel_calling.perform_indel_calling_selector
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.exclude_read_group.exclude_read_groups=samtools_mpileup-7-advanced_options.exclude_read_group.exclude_read_groups
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.extended_BAQ_computation=samtools_mpileup-7-advanced_options.extended_BAQ_computation
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.minimum_mapping_quality=samtools_mpileup-7-advanced_options.minimum_mapping_quality
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.region_string=samtools_mpileup-7-advanced_options.region_string
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.advanced_options_selector=samtools_mpileup-7-advanced_options.advanced_options_selector
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.filter_by_flags.filter_flags=samtools_mpileup-7-advanced_options.filter_by_flags.filter_flags
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.max_reads_per_bam=samtools_mpileup-7-advanced_options.max_reads_per_bam
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.skip_anomalous_read_pairs=samtools_mpileup-7-advanced_options.skip_anomalous_read_pairs
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.ignore_overlaps=samtools_mpileup-7-advanced_options.ignore_overlaps
pipeline.parameters.snvphyl.samtools_mpileup-7-advanced_options.limit_by_region.limit_by_regions=samtools_mpileup-7-advanced_options.limit_by_region.limit_by_regions
pipeline.parameters.snvphyl.samtools_mpileup-7-genotype_likelihood_computation_type.compressed=samtools_mpileup-7-genotype_likelihood_computation_type.compressed
#Tool Parameters - Tool: bcftools_call - Workflow Step #: 9
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.bcftools_call-9-variants_only=Output variants only (-v)
pipeline.parameters.snvphyl.bcftools_call-9-skip_indels_snvs=Skip (-V)
pipeline.parameters.snvphyl.bcftools_call-9-output_format=bcftools_call-9-output_format
pipeline.parameters.snvphyl.bcftools_call-9-constraint=Constraint (-C)
pipeline.parameters.snvphyl.bcftools_call-9-caller=Caller
pipeline.parameters.snvphyl.bcftools_call-9-filter_calls.filter_calls_selector=bcftools_call-9-filter_calls.filter_calls_selector
#Tool Parameters - Tool: bcftools_view - Workflow Step #: 10
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.bcftools_view-10-output_format=bcftools_view-10-output_format
pipeline.parameters.snvphyl.bcftools_view-10-samples=Samples to include or exclude
pipeline.parameters.snvphyl.bcftools_view-10-trim_alt_alleles=Trim alternate alleles
pipeline.parameters.snvphyl.bcftools_view-10-filters=FILTER strings
pipeline.parameters.snvphyl.bcftools_view-10-max_nref=Maximum count for non reference
pipeline.parameters.snvphyl.bcftools_view-10-region=Region to view
pipeline.parameters.snvphyl.bcftools_view-10-sites_no_genotype=Sites without a called genotype
pipeline.parameters.snvphyl.bcftools_view-10-min_nref=Minimum count for non reference
pipeline.parameters.snvphyl.bcftools_view-10-header_option=Choose the output everything, only header or no header
#Tool Parameters - Tool: consolidate_vcfs - Workflow Step #: 11
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.consolidate_vcfs-11-coverage=Minimum coverage
pipeline.parameters.snvphyl.consolidate_vcfs-11-use_density_filter.window_size=Size of search window
pipeline.parameters.snvphyl.consolidate_vcfs-11-use_density_filter.select_list=SNV density filtering
pipeline.parameters.snvphyl.consolidate_vcfs-11-mean_mapping=Minimum mean mapping quality
pipeline.parameters.snvphyl.consolidate_vcfs-11-snv_abundance_ratio=SNV abundance ratio
pipeline.parameters.snvphyl.consolidate_vcfs-11-use_density_filter.threshold=Density threshold cutoff
#Tool Parameters - Tool: vcf2snvalignment - Workflow Step #: 14
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.vcf2snvalignment-14-strain_list.select_list=vcf2snvalignment-14-strain_list.select_list
pipeline.parameters.snvphyl.vcf2snvalignment-14-reference=vcf2snvalignment-14-reference
#Tool Parameters - Tool: phyml - Workflow Step #: 17
#Tue Aug 14 16:47:07 CDT 2018
pipeline.parameters.snvphyl.phyml-17-search=Tree topology search operation
pipeline.parameters.snvphyl.phyml-17-support_condition.support=Branch support
pipeline.parameters.snvphyl.phyml-17-random_condition.random=Random starting points
pipeline.parameters.snvphyl.phyml-17-prop_invar=Proportion of invariant sites
pipeline.parameters.snvphyl.phyml-17-datatype_condition.tstv=Transition/transversion ratio
pipeline.parameters.snvphyl.phyml-17-gamma_condition.gamma=Discrete gamma model
pipeline.parameters.snvphyl.phyml-17-datatype_condition.model=Evolution model
pipeline.parameters.snvphyl.phyml-17-gamma_condition.shape=Shape parameter of the gamma model
pipeline.parameters.snvphyl.phyml-17-datatype_condition.type=Data type
pipeline.parameters.snvphyl.phyml-17-gamma_condition.categories=Number of categories for the discrete gamma model
```


**Contents of `irida_workflow.xml`:**

*Note: You may need to remove some of the tool parameters from the `irida_workflow.xml` file.*

```xml
<?xml version="1.0" encoding="UTF-8"?>
<iridaWorkflow>
  <id>ece298c1-cd9d-4aad-a6ce-a366bd5cbb9a</id>
  <name>SNVPhyl</name>
  <version>1.0.1</version>
  <analysisType>phylogenomics</analysisType>
  <inputs>
    <sequenceReadsPaired>sequence_reads_paired</sequenceReadsPaired>
    <reference>reference</reference>
    <requiresSingleSample>false</requiresSingleSample>
  </inputs>
  <parameters>
    <parameter name="smalt_index-2-k" defaultValue="13">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index/smalt_index/1.1.0" parameterName="k" label="K-mer size" type="integer"/>
    </parameter>
    <parameter name="smalt_index-2-s" defaultValue="6">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index/smalt_index/1.1.0" parameterName="s" label="Step size" type="integer"/>
    </parameter>
    <parameter name="find_repeats-3-pid" defaultValue="90">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats/findrepeat/1.8.0" parameterName="pid"/>
    </parameter>
    <parameter name="find_repeats-3-length" defaultValue="150">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats/findrepeat/1.8.0" parameterName="length"/>
    </parameter>
    <parameter name="smalt_map-4-mincover" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="mincover" label="Mincover" type="text"/>
    </parameter>
    <parameter name="smalt_map-4-sw_weighted" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="sw_weighted" label="Smith-Waterman scores are complexity weighted." type="boolean"/>
    </parameter>
    <parameter name="smalt_map-4-minid" defaultValue="0.5">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minid" label="Sets an identity threshold for a mapping to be reported (default: 0)." type="text"/>
    </parameter>
    <parameter name="smalt_map-4-insertmax" defaultValue="1000">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="insertmax" label="Maximum insert size (only in paired-end mode). " type="text"/>
    </parameter>
    <parameter name="smalt_map-4-minscor" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minscor" label="Sets an absolute threshold of the Smith-Waterman scores." type="text"/>
    </parameter>
    <parameter name="smalt_map-4-seed" defaultValue="1">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="seed" label="If the there are multiple mappings with the same best alignment score report one picked at random." type="text"/>
    </parameter>
    <parameter name="smalt_map-4-oformat.outformat" defaultValue="bam">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="oformat.outformat" label="Format" type="select"/>
    </parameter>
    <parameter name="smalt_map-4-minbasq" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minbasq" label="Sets a base quality threshold (0 &lt;= minbasq &lt;= 10, default 0)" type="text"/>
    </parameter>
    <parameter name="smalt_map-4-insertmin" defaultValue="20">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="insertmin" label="Minimum insert size (only in paired-end mode). " type="text"/>
    </parameter>
    <parameter name="smalt_map-4-singlePaired.pairtype" defaultValue="pe">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="singlePaired.pairtype" label="Pair Type" type="select"/>
    </parameter>
    <parameter name="smalt_map-4-search_harder" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="search_harder" label="This flag triggers a more exhaustive search for alignments at the cost of decreased speed" type="boolean"/>
    </parameter>
    <parameter name="smalt_map-4-singlePaired.sPaired" defaultValue="collections">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="singlePaired.sPaired" label="What is the library type?" type="select"/>
    </parameter>
    <parameter name="smalt_map-4-scordiff" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="scordiff" label="Scordiff" type="text"/>
    </parameter>
    <parameter name="verify_map-5-minmap" defaultValue="80">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/verify_map/verify_map/1.8.0" parameterName="minmap"/>
    </parameter>
    <parameter name="verify_map-5-mindepth" defaultValue="${min_coverage}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/verify_map/verify_map/1.8.0" parameterName="mindepth"/>
    </parameter>
    <parameter name="freebayes-6-options_type.algorithmic_features.algorithmic_features_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.algorithmic_features.algorithmic_features_selector" label="Tweak algorithmic features?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_mappability_priors.population_mappability_priors_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_mappability_priors.population_mappability_priors_selector" label="Set population and mappability priors?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.reference_allele.reference_allele_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.reference_allele.reference_allele_selector" label="Use reference allele?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.T" defaultValue="0.001">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.T" label="The expected mutation rate or pairwise nucleotide diversity among the population under analysis" type="float"/>
    </parameter>
    <parameter name="freebayes-6-options_type.input_filters.input_filters_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.input_filters.input_filters_selector" label="Set input filters?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.population_model_selector" defaultValue="True">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.population_model_selector" label="Set population model?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.optional_inputs.optional_inputs_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.optional_inputs.optional_inputs_selector" label="Do you want to provide additional inputs?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-reference_source.reference_source_selector" defaultValue="history">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="reference_source.reference_source_selector" label="Load reference genome from" type="select"/>
    </parameter>
    <parameter name="freebayes-6-target_limit_type.target_limit_type_selector" defaultValue="do_not_limit">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="target_limit_type.target_limit_type_selector" label="Limit variant calling to a set of regions?" type="select"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.J" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.J" label="Assume that samples result from pooled sequencing" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.K" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.K" label="Output all alleles which pass input filters, regardles of genotyping outcome or model" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.genotype_likelihoods.genotype_likelihoods_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.genotype_likelihoods.genotype_likelihoods_selector" label="Tweak genotype likelihoods?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.O" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.O" label="Turn off left-alignment of indels?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.options_type_selector" defaultValue="full">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.options_type_selector" label="Choose parameter selection level" type="select"/>
    </parameter>
    <parameter name="freebayes-6-options_type.reporting.reporting_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.reporting.reporting_selector" label="Set reporting option?" type="boolean"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.P" defaultValue="1">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.P" label="Set ploidy for the analysis" type="integer"/>
    </parameter>
    <parameter name="freebayes-6-options_type.allele_scope.allele_scope_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.allele_scope.allele_scope_selector" label="Set allelic scope?" type="boolean"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.minimum_mapping_quality" defaultValue="0">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.minimum_mapping_quality"/>
    </parameter>
    <parameter name="samtools_mpileup-7-genotype_likelihood_computation_type.compressed" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="genotype_likelihood_computation_type.compressed"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.region_string" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.region_string"/>
    </parameter>
    <parameter name="samtools_mpileup-7-genotype_likelihood_computation_type.perform_indel_calling.perform_indel_calling_selector" defaultValue="do_not_perform_indel_calling">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="genotype_likelihood_computation_type.perform_indel_calling.perform_indel_calling_selector"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.limit_by_region.limit_by_regions" defaultValue="no_limit">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.limit_by_region.limit_by_regions"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.coefficient_for_downgrading" defaultValue="0">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.coefficient_for_downgrading"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.advanced_options_selector" defaultValue="advanced">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.advanced_options_selector"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.exclude_read_group.exclude_read_groups" defaultValue="no_limit">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.exclude_read_group.exclude_read_groups"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.skip_anomalous_read_pairs" defaultValue="True">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.skip_anomalous_read_pairs"/>
    </parameter>
    <parameter name="samtools_mpileup-7-genotype_likelihood_computation_type.output_format" defaultValue="--VCF">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="genotype_likelihood_computation_type.output_format"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.filter_by_flags.filter_flags" defaultValue="nofilter">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.filter_by_flags.filter_flags"/>
    </parameter>
    <parameter name="samtools_mpileup-7-reference_source.reference_source_selector" defaultValue="history">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="reference_source.reference_source_selector"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.minimum_base_quality" defaultValue="0">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.minimum_base_quality"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.max_reads_per_bam" defaultValue="1024">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.max_reads_per_bam"/>
    </parameter>
    <parameter name="samtools_mpileup-7-genotype_likelihood_computation_type.genotype_likelihood_computation_type_selector" defaultValue="perform_genotype_likelihood_computation">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="genotype_likelihood_computation_type.genotype_likelihood_computation_type_selector"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.ignore_overlaps" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.ignore_overlaps"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.disable_probabilistic_realignment" defaultValue="True">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.disable_probabilistic_realignment"/>
    </parameter>
    <parameter name="samtools_mpileup-7-advanced_options.extended_BAQ_computation" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup/samtools_mpileup/2.0" parameterName="advanced_options.extended_BAQ_computation"/>
    </parameter>
    <parameter name="bcftools_call-9-output_format" defaultValue="b">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="output_format"/>
    </parameter>
    <parameter name="bcftools_call-9-skip_indels_snvs" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="skip_indels_snvs" label="Skip (-V)" type="select"/>
    </parameter>
    <parameter name="bcftools_call-9-variants_only" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="variants_only" label="Output variants only (-v)" type="boolean"/>
    </parameter>
    <parameter name="bcftools_call-9-filter_calls.filter_calls_selector" defaultValue="no_filter">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="filter_calls.filter_calls_selector"/>
    </parameter>
    <parameter name="bcftools_call-9-caller" defaultValue="-c">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="caller" label="Caller" type="select"/>
    </parameter>
    <parameter name="bcftools_call-9-constraint" defaultValue="-C alleles">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="constraint" label="Constraint (-C)" type="select"/>
    </parameter>
    <parameter name="bcftools_view-10-trim_alt_alleles" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="trim_alt_alleles" label="Trim alternate alleles" type="select"/>
    </parameter>
    <parameter name="bcftools_view-10-min_nref" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="min_nref" label="Minimum count for non reference" type="integer"/>
    </parameter>
    <parameter name="bcftools_view-10-output_format" defaultValue="b">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="output_format"/>
    </parameter>
    <parameter name="bcftools_view-10-sites_no_genotype" defaultValue="off">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="sites_no_genotype" label="Sites without a called genotype" type="select"/>
    </parameter>
    <parameter name="bcftools_view-10-max_nref" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="max_nref" label="Maximum count for non reference" type="integer"/>
    </parameter>
    <parameter name="bcftools_view-10-filters" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="filters" label="FILTER strings" type="text"/>
    </parameter>
    <parameter name="bcftools_view-10-region" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="region" label="Region to view" type="text"/>
    </parameter>
    <parameter name="bcftools_view-10-header_option" defaultValue="all">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="header_option" label="Choose the output everything, only header or no header" type="select"/>
    </parameter>
    <parameter name="bcftools_view-10-samples" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="samples" label="Samples to include or exclude" type="text"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.select_list" defaultValue="yes">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.select_list" label="SNV density filtering" type="select"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.threshold" defaultValue="2">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.threshold" label="Density threshold cutoff" type="integer"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.window_size" defaultValue="500">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.window_size" label="Size of search window" type="integer"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-mean_mapping" defaultValue="${min_mean_mapping}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="mean_mapping" label="Minimum mean mapping quality" type="integer"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-coverage" defaultValue="${min_coverage}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="coverage" label="Minimum coverage" type="integer"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-snv_abundance_ratio" defaultValue="${snv_abundance_ratio}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="snv_abundance_ratio" label="SNV abundance ratio" type="text"/>
    </parameter>
    <parameter name="vcf2snvalignment-14-strain_list.select_list" defaultValue="all">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment/vcf2snvalignment/1.8.0" parameterName="strain_list.select_list"/>
    </parameter>
    <parameter name="vcf2snvalignment-14-reference" defaultValue="reference">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment/vcf2snvalignment/1.8.0" parameterName="reference"/>
    </parameter>
    <parameter name="phyml-17-prop_invar" defaultValue="0.0">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="prop_invar" label="Proportion of invariant sites" type="text"/>
    </parameter>
    <parameter name="phyml-17-random_condition.random" defaultValue="no">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="random_condition.random" label="Random starting points" type="select"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.model" defaultValue="GTR">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.model" label="Evolution model" type="select"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.shape" defaultValue="e">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.shape" label="Shape parameter of the gamma model" type="text"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.gamma" defaultValue="yes">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.gamma" label="Discrete gamma model" type="select"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.categories" defaultValue="4">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.categories" label="Number of categories for the discrete gamma model" type="text"/>
    </parameter>
    <parameter name="phyml-17-support_condition.support" defaultValue="sh">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="support_condition.support" label="Branch support" type="select"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.tstv" defaultValue="e">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.tstv" label="Transition/transversion ratio" type="text"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.type" defaultValue="nt">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.type" label="Data type" type="select"/>
    </parameter>
    <parameter name="phyml-17-search" defaultValue="BEST">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="search" label="Tree topology search operation" type="select"/>
    </parameter>
  </parameters>
  <outputs>
    <output name="invalid_positions.bed" fileName="invalid_positions.bed"/>
    <output name="mappingQuality.txt" fileName="mappingQuality.txt"/>
    <output name="mpileup_bcf" fileName="mpileup_bcf"/>
    <output name="filtered_freebayes_bcf" fileName="filtered_freebayes_bcf"/>
    <output name="combined_invalid_positions" fileName="combined_invalid_positions"/>
    <output name="snvTable.tsv" fileName="snvTable.tsv"/>
    <output name="vcf2core.tsv" fileName="vcf2core.tsv"/>
    <output name="snvAlignment.phy" fileName="snvAlignment.phy"/>
    <output name="filterStats.txt" fileName="filterStats.txt"/>
    <output name="phylogeneticTreeStats.txt" fileName="phylogeneticTreeStats.txt"/>
    <output name="phylogeneticTree.newick" fileName="phylogeneticTree.newick"/>
    <output name="snvMatrix.tsv" fileName="snvMatrix.tsv"/>
  </outputs>
  <toolRepositories>
    <repository>
      <name>smalt_map</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>62faa1906fd7</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map-->
    </repository>
    <repository>
      <name>freebayes</name>
      <owner>devteam</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>85b0800adde2</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes-->
    </repository>
    <repository>
      <name>smalt_index</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>1e249f90f2a6</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index-->
    </repository>
    <repository>
      <name>filter_stats</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>416082f5d12d</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/filter_stats-->
    </repository>
    <repository>
      <name>filter_vcf</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>dc4f6d16339c</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/filter_vcf-->
    </repository>
    <repository>
      <name>bcftools_call</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>98f8a44014c1</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call-->
    </repository>
    <repository>
      <name>snv_matrix</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>ef76fcf5a209</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/snv_matrix-->
    </repository>
    <repository>
      <name>phyml</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>fdb1cddf0dbd</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/phyml-->
    </repository>
    <repository>
      <name>find_repeats</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>ab7b9169e516</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats-->
    </repository>
    <repository>
      <name>vcf2snvalignment</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>4c99f0d32983</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment-->
    </repository>
    <repository>
      <name>collapse_collections</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>7e5a39b5dad3</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/collapse_collections-->
    </repository>
    <repository>
      <name>verify_map</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>c50b63008cc9</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/verify_map-->
    </repository>
    <repository>
      <name>regex_find_replace</name>
      <owner>jjohnson</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>3205c22bc968</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/jjohnson/regex_find_replace-->
    </repository>
    <repository>
      <name>samtools_mpileup</name>
      <owner>devteam</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>81948c6e4d4d</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup-->
    </repository>
    <repository>
      <name>bcftools_view</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>3628f89cb4ee</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view-->
    </repository>
    <repository>
      <name>consolidate_vcfs</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>924d0f651195</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs-->
    </repository>
  </toolRepositories>
</iridaWorkflow>
```


### Bugs

Please create an issue on Github if you encounter any issues!

Stacktraces of exceptions and input files and command-line arguments producing errors very much appreciated!

## Legal


Copyright Government of Canada 2018

Written by: National Microbiology Laboratory, Public Health Agency of Canada

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this work except in compliance with the License. You may obtain a copy of the
License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.


## Contact

**Gary van Domselaar**: gary.vandomselaar@phac-aspc.gc.ca

