# Overview

RF2-D2 is a build and packaging tool for [SNOMED CT](https://snomed.org) [RF2](http://snomed.org/rfs) releases. With RF2-D2, you use a YAML file to configure your RF2 release bundle's directory structure, file naming and custom file formats. Then, with a single command, you verify, create and transform release files from your configuration. Here is a complete list of features RF2-D2 provides:

* Validation and verification ([rf2 check](check.md))
	* Verify existing RF2 Release file names and structure
    * Validate SNOMED CT content using a number of validation rules (WIP)
* Packaging ([rf2 create](create.md))
    * Package a single RF2 Release from one or more RF2 content, documentation and resource files  
    * Configure and customize the name of the RF2 Release and RF2 files
    * Configure release structure and add additional content like custom files, documentation and resources
* Transformation ([rf2 transform](transform.md))
    * Filter out certain parts from existing RF2 files (for example exclude all content from a module, etc.)
    * Replace values in RF2 files using conditions, simple statements and even external scripts 
* Diffing ([rf2 diff](diff.md))
    * Diff RF2 text files and releases (WIP)

Using RF2-D2 is usually a three step process:

1. Define your RF2 Release format in an `rf2-spec.yml`, so it can be reused and reproduced anywhere
2. Specify and copy the necessary RF2 source files to the directory where the `rf2-spec.yml` is defined
3. Run the desired command

An `rf2-spec.yml` file looks like this (see full file [here](https://github.com/b2ihealthcare/rf2-d2/blob/master/src/main/resources/rf2-spec.yml)):

```
version: "1.0"
rf2Version: "20190131"
release:
  initial: "SnomedCT"
  product: ''
  format: "RF2"
  status: "PRODUCTION"
  date: ''
  time: ''
  country: "INT"
  namespace: ''
  contentSubType:
    - "Full"
    - "Snapshot"
    - "Delta"
  content:
    Terminology:
      files:
        - contentType: Concept
          header: [id, effectiveTime, active, moduleId, definitionStatusId]
        - contentType: Description
          languageCode: en
          header: [id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId]
    Refset:
      Content:
        files:
          - contentType: Refset
            summary: Simple
            header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId]
      Language:
        files:
          - contentType: cRefset
            summary: Language
            languageCode: en
            header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, acceptabilityId]
      Map:
        files:
          - contentType: sRefset
            summary: SimpleMap
            header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, mapTarget]
      Metadata:
        files:
          - contentType: ciRefset
            summary: DescriptionType
            header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, descriptionFormat, descriptionLength]
          - contentType: ssRefset
            summary: ModuleDependency
            header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, sourceEffectiveTime, targetEffectiveTime]
```

For more information about the Specification file, see the `rf2-spec.yml` file [reference](spec/index.md).

In the next section we will take a look at the available commands you can execute with RF2-D2.
