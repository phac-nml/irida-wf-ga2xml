# irida-wf-ga2xml

[![Build Status](https://travis-ci.org/phac-nml/irida-wf-ga2xml.svg?branch=master)](https://travis-ci.org/phac-nml/irida-wf-ga2xml)

Create an IRIDA XML workflow description from a Galaxy workflow ga JSON file. 

## Installation

- Java 1.8 must be installed 
- Download [latest standalone release of `irida-wf-ga2xml`](https://github.com/phac-nml/irida-wf-ga2xml/releases)

## Usage

Execute standalone JAR with `java`:

    $ java -jar irida-wf-ga2xml-0.1.0-standalone.jar -h
    
    Output IRIDA workflow XML file from Galaxy Workflow ga JSON file
    
    Usage: irida_wf_ga2xml [options] > irida_workflow.xml
    
    Options:
      -n, --workflow-name WORKFLOW_NAME                 Workflow name (default is to extract name from workflow input file)
      -t, --analysis-type ANALYSIS_TYPE        DEFAULT  IRIDA AnalysisType
      -W, --workflow-version WORKFLOW_VERSION  0.1.0    Workflow version
      -m, --multi-sample                                Multiple sample workflow; not a single sample workflow
      -i, --input INPUT                                 Galaxy workflow ga JSON format file
      -h, --help

## Options

- `-n`/`--workflow-name`: Workflow name to use in IRIDA workflow XML
- `-t`/`--analysis-type`: IRIDA AnalysisType enum (e.g. "phylogenomics")
- `-W`/`--workflow-version`: Galaxy workflow version number (e.g. "0.1.0")
- `-m`: Flag for whether the workflow operates on multiple samples (e.g. SNVPhyl does use multiple samples to generate a phylogenetic tree; SISTR operates on single samples to produce an individual result for each sample)
- `-i`/`--input`: Galaxy workflow specifiction file (e.g. `workflow.ga`); JSON format expected

## Examples

    $ java -jar irida-wf-ga2xml-1.0.0-SNAPSHOT-standalone.jar \
        -t phylogenomics \ 
        -W 1.0.1 \ 
        -m \
        -i test/data/snvphyl-1.0.1-workflow.ga \
        -n snvphyl
        -o /tmp

Output:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<iridaWorkflow>
  <id>de61ed2e-289a-4ba9-95d3-e91871bc5f80</id>
  <name>snvphyl</name>
  <version>1.0.1</version>
  <analysisType>phylogenomics</analysisType>
  <inputs>
    <sequenceReadsPaired>sequence_reads_paired</sequenceReadsPaired>
    <reference>reference</reference>
    <requiresSingleSample>false</requiresSingleSample>
  </inputs>
  <parameters>
    <parameter name="smalt_index-2-k" defaultValue="13">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index/smalt_index/1.1.0" parameterName="k"/>
    </parameter>
    <parameter name="smalt_index-2-s" defaultValue="6">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index/smalt_index/1.1.0" parameterName="s"/>
    </parameter>
    <parameter name="find_repeats-3-pid" defaultValue="90">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats/findrepeat/1.8.0" parameterName="pid"/>
    </parameter>
    <parameter name="find_repeats-3-length" defaultValue="150">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats/findrepeat/1.8.0" parameterName="length"/>
    </parameter>
    <parameter name="smalt_map-4-mincover" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="mincover"/>
    </parameter>
    <parameter name="smalt_map-4-sw_weighted" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="sw_weighted"/>
    </parameter>
    <parameter name="smalt_map-4-minid" defaultValue="0.5">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minid"/>
    </parameter>
    <parameter name="smalt_map-4-insertmax" defaultValue="1000">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="insertmax"/>
    </parameter>
    <parameter name="smalt_map-4-minscor" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minscor"/>
    </parameter>
    <parameter name="smalt_map-4-seed" defaultValue="1">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="seed"/>
    </parameter>
    <parameter name="smalt_map-4-oformat.outformat" defaultValue="bam">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="oformat.outformat"/>
    </parameter>
    <parameter name="smalt_map-4-minbasq" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="minbasq"/>
    </parameter>
    <parameter name="smalt_map-4-insertmin" defaultValue="20">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="insertmin"/>
    </parameter>
    <parameter name="smalt_map-4-singlePaired.pairtype" defaultValue="pe">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="singlePaired.pairtype"/>
    </parameter>
    <parameter name="smalt_map-4-search_harder" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="search_harder"/>
    </parameter>
    <parameter name="smalt_map-4-singlePaired.sPaired" defaultValue="collections">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="singlePaired.sPaired"/>
    </parameter>
    <parameter name="smalt_map-4-scordiff" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map/smalt_map/1.1.0" parameterName="scordiff"/>
    </parameter>
    <parameter name="verify_map-5-minmap" defaultValue="80">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/verify_map/verify_map/1.8.0" parameterName="minmap"/>
    </parameter>
    <parameter name="verify_map-5-mindepth" defaultValue="${min_coverage}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/verify_map/verify_map/1.8.0" parameterName="mindepth"/>
    </parameter>
    <parameter name="freebayes-6-options_type.algorithmic_features.algorithmic_features_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.algorithmic_features.algorithmic_features_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_mappability_priors.population_mappability_priors_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_mappability_priors.population_mappability_priors_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.reference_allele.reference_allele_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.reference_allele.reference_allele_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.T" defaultValue="0.001">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.T"/>
    </parameter>
    <parameter name="freebayes-6-options_type.input_filters.input_filters_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.input_filters.input_filters_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.population_model_selector" defaultValue="True">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.population_model_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.optional_inputs.optional_inputs_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.optional_inputs.optional_inputs_selector"/>
    </parameter>
    <parameter name="freebayes-6-reference_source.reference_source_selector" defaultValue="history">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="reference_source.reference_source_selector"/>
    </parameter>
    <parameter name="freebayes-6-target_limit_type.target_limit_type_selector" defaultValue="do_not_limit">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="target_limit_type.target_limit_type_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.J" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.J"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.K" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.K"/>
    </parameter>
    <parameter name="freebayes-6-options_type.genotype_likelihoods.genotype_likelihoods_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.genotype_likelihoods.genotype_likelihoods_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.O" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.O"/>
    </parameter>
    <parameter name="freebayes-6-options_type.options_type_selector" defaultValue="full">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.options_type_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.reporting.reporting_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.reporting.reporting_selector"/>
    </parameter>
    <parameter name="freebayes-6-options_type.population_model.P" defaultValue="1">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.population_model.P"/>
    </parameter>
    <parameter name="freebayes-6-options_type.allele_scope.allele_scope_selector" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes/freebayes/0.4.1" parameterName="options_type.allele_scope.allele_scope_selector"/>
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
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="skip_indels_snvs"/>
    </parameter>
    <parameter name="bcftools_call-9-variants_only" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="variants_only"/>
    </parameter>
    <parameter name="bcftools_call-9-filter_calls.filter_calls_selector" defaultValue="no_filter">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="filter_calls.filter_calls_selector"/>
    </parameter>
    <parameter name="bcftools_call-9-caller" defaultValue="-c">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="caller"/>
    </parameter>
    <parameter name="bcftools_call-9-constraint" defaultValue="-C alleles">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_call/bcftools_call/1.0.1" parameterName="constraint"/>
    </parameter>
    <parameter name="bcftools_view-10-trim_alt_alleles" defaultValue="False">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="trim_alt_alleles"/>
    </parameter>
    <parameter name="bcftools_view-10-min_nref" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="min_nref"/>
    </parameter>
    <parameter name="bcftools_view-10-output_format" defaultValue="b">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="output_format"/>
    </parameter>
    <parameter name="bcftools_view-10-sites_no_genotype" defaultValue="off">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="sites_no_genotype"/>
    </parameter>
    <parameter name="bcftools_view-10-max_nref" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="max_nref"/>
    </parameter>
    <parameter name="bcftools_view-10-filters" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="filters"/>
    </parameter>
    <parameter name="bcftools_view-10-region" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="region"/>
    </parameter>
    <parameter name="bcftools_view-10-header_option" defaultValue="all">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="header_option"/>
    </parameter>
    <parameter name="bcftools_view-10-samples" defaultValue="">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/bcftools_view/bcftools_view/0.1.1" parameterName="samples"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.select_list" defaultValue="yes">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.select_list"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.threshold" defaultValue="2">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.threshold"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-use_density_filter.window_size" defaultValue="500">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="use_density_filter.window_size"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-mean_mapping" defaultValue="${min_mean_mapping}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="mean_mapping"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-coverage" defaultValue="${min_coverage}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="coverage"/>
    </parameter>
    <parameter name="consolidate_vcfs-11-snv_abundance_ratio" defaultValue="${snv_abundance_ratio}">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/consolidate_vcfs/consolidate_vcfs/1.8.0" parameterName="snv_abundance_ratio"/>
    </parameter>
    <parameter name="vcf2snvalignment-14-strain_list.select_list" defaultValue="all">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment/vcf2snvalignment/1.8.0" parameterName="strain_list.select_list"/>
    </parameter>
    <parameter name="vcf2snvalignment-14-reference" defaultValue="reference">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment/vcf2snvalignment/1.8.0" parameterName="reference"/>
    </parameter>
    <parameter name="phyml-17-prop_invar" defaultValue="0.0">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="prop_invar"/>
    </parameter>
    <parameter name="phyml-17-random_condition.random" defaultValue="no">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="random_condition.random"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.model" defaultValue="GTR">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.model"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.shape" defaultValue="e">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.shape"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.gamma" defaultValue="yes">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.gamma"/>
    </parameter>
    <parameter name="phyml-17-gamma_condition.categories" defaultValue="4">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="gamma_condition.categories"/>
    </parameter>
    <parameter name="phyml-17-support_condition.support" defaultValue="sh">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="support_condition.support"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.tstv" defaultValue="e">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.tstv"/>
    </parameter>
    <parameter name="phyml-17-datatype_condition.type" defaultValue="nt">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="datatype_condition.type"/>
    </parameter>
    <parameter name="phyml-17-search" defaultValue="BEST">
      <toolParameter toolId="irida.corefacility.ca/galaxy-shed/repos/nml/phyml/phyml1/3.1.1" parameterName="search"/>
    </parameter>
  </parameters>
  <outputs>
    <output name="out" fileName="invalid_positions.bed"/>
    <output name="output_log" fileName="mappingQuality.txt"/>
    <output name="output" fileName="mpileup_bcf"/>
    <output name="output" fileName="filtered_freebayes_bcf"/>
    <output name="out_file1" fileName="combined_invalid_positions"/>
    <output name="positions" fileName="snvTable.tsv"/>
    <output name="vcf2core" fileName="vcf2core.tsv"/>
    <output name="out_file1" fileName="snvAlignment.phy"/>
    <output name="out" fileName="filterStats.txt"/>
    <output name="output_stats" fileName="phylogeneticTreeStats.txt"/>
    <output name="output_tree" fileName="phylogeneticTree.newick"/>
    <output name="out" fileName="snvMatrix.tsv"/>
  </outputs>
  <toolRepositories>
    <repository>
      <name>smalt_index</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>1e249f90f2a6</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/smalt_index-->
    </repository>
    <repository>
      <name>find_repeats</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>ab7b9169e516</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/find_repeats-->
    </repository>
    <repository>
      <name>smalt_map</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>62faa1906fd7</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/smalt_map-->
    </repository>
    <repository>
      <name>verify_map</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>c50b63008cc9</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/verify_map-->
    </repository>
    <repository>
      <name>freebayes</name>
      <owner>devteam</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>85b0800adde2</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/devteam/freebayes-->
    </repository>
    <repository>
      <name>samtools_mpileup</name>
      <owner>devteam</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>81948c6e4d4d</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/devteam/samtools_mpileup-->
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
    <repository>
      <name>collapse_collections</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>7e5a39b5dad3</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/collapse_collections-->
    </repository>
    <repository>
      <name>vcf2snvalignment</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>4c99f0d32983</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/vcf2snvalignment-->
    </repository>
    <repository>
      <name>regex_find_replace</name>
      <owner>jjohnson</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>3205c22bc968</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/jjohnson/regex_find_replace-->
    </repository>
    <repository>
      <name>filter_stats</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>416082f5d12d</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/filter_stats-->
    </repository>
    <repository>
      <name>phyml</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>fdb1cddf0dbd</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/phyml-->
    </repository>
    <repository>
      <name>snv_matrix</name>
      <owner>nml</owner>
      <url>https://irida.corefacility.ca/galaxy-shed</url>
      <revision>ef76fcf5a209</revision>
      <!--WARNING: Latest revision fetched from https://irida.corefacility.ca/galaxy-shed/repos/nml/snv_matrix-->
    </repository>
  </toolRepositories>
</iridaWorkflow>


```


### Bugs

Please create an issue on Github if you encounter any issues!

Stacktraces of exceptions and input producing errors very much appreciated!

## Legal


Copyright Government of Canada 2017

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

