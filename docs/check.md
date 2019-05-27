# Check command

This page provides the usage information for the `rf2 check` command.

## Command options overview and help

You can also see this information by running `rf2 help check` from the command line.

```
Usage:

rf2 check PATH...

Description:

Checks a set of RF2 files and/or archives against the current RF2 Specification

Parameters:
      PATH...   RF2 source files to check.
```

## PATH argument

The `rf2 check` command accepts arbitrary number of RF2 source file paths. The `PATH` argument must point to a location of file (_NOTE: directories are not supported yet_).

For example, consider this command line:

    rf2 check SnomedCT_RF2_PRODUCTION_20190131T120000Z.zip

Executing it will print out the RF2 release structure along with certain properties of each file inside the release package. Example output:

```
SnomedCT_InternationalRF2_PRODUCTION_20190131T120000Z.zip
  -type: Release
  SnomedCT_InternationalRF2_PRODUCTION_20190131T120000Z
    -type: Directory
    Snapshot
      -type: Directory
      Refset
        -type: Directory
        Metadata
          -type: Directory
          der2_cRefset_MRCMModuleScopeSnapshot_INT_20190131.txt
            -type: cRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, mrcmRuleRefsetId]
            -lines: 3
          der2_ssccRefset_MRCMAttributeRangeSnapshot_INT_20190131.txt
            -type: ssccRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, rangeConstraint, attributeRule, ruleStrengthId, contentTypeId]
            -lines: 119
          der2_cissccRefset_MRCMAttributeDomainSnapshot_INT_20190131.txt
            -type: cissccRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, domainId, grouped, attributeCardinality, attributeInGroupCardinality, ruleStrengthId, contentTypeId]
            -lines: 134
          der2_sssssssRefset_MRCMDomainSnapshot_INT_20190131.txt
            -type: sssssssRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, domainConstraint, parentDomain, proximalPrimitiveConstraint, proximalPrimitiveRefinement, domainTemplateForPrecoordination, domainTemplateForPostcoordination, guideURL]
            -lines: 19
          der2_ciRefset_DescriptionTypeSnapshot_INT_20190131.txt
            -type: ciRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, descriptionFormat, descriptionLength]
            -lines: 3
          der2_ssRefset_ModuleDependencySnapshot_INT_20190131.txt
            -type: ssRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, sourceEffectiveTime, targetEffectiveTime]
            -lines: 3
          der2_cciRefset_RefsetDescriptorSnapshot_INT_20190131.txt
            -type: cciRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, attributeDescription, attributeType, attributeOrder]
            -lines: 167
        Language
          -type: Directory
          der2_cRefset_LanguageSnapshot-en_INT_20190131.txt
            -type: cRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, acceptabilityId]
            -lines: 2831149
        Map
          -type: Directory
          der2_iisssccRefset_ExtendedMapSnapshot_INT_20190131.txt
            -type: iisssccRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, mapGroup, mapPriority, mapRule, mapAdvice, mapTarget, correlationId, mapCategoryId]
            -lines: 174594
          der2_sRefset_SimpleMapSnapshot_INT_20190131.txt
            -type: sRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, mapTarget]
            -lines: 493422
        Content
          -type: Directory
          der2_Refset_SimpleSnapshot_INT_20190131.txt
            -type: Refset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId]
            -lines: 19878
          der2_cRefset_AttributeValueSnapshot_INT_20190131.txt
            -type: cRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, valueId]
            -lines: 535131
          der2_cRefset_AssociationSnapshot_INT_20190131.txt
            -type: cRefset
            -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, targetComponentId]
            -lines: 157369
      Terminology
        -type: Directory
        sct2_sRefset_OWLExpressionSnapshot_INT_20190131.txt
          -type: sRefset
          -header: [id, effectiveTime, active, moduleId, refsetId, referencedComponentId, owlExpression]
          -lines: 224
        sct2_TextDefinition_Snapshot-en_INT_20190131.txt
          -type: TextDefinition
          -header: [id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId]
          -lines: 5726
        sct2_Identifier_Snapshot_INT_20190131.txt
          -type: Identifier
          -header: [identifierSchemeId, alternateIdentifier, effectiveTime, active, moduleId, referencedComponentId]
          -lines: 0
          -issues:
            -WARN: No validator is registered for column header 'identifierSchemeId'.
            -WARN: No validator is registered for column header 'alternateIdentifier'.
        sct2_Relationship_Snapshot_INT_20190131.txt
          -type: Relationship
          -header: [id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId]
          -lines: 2876521
        sct2_StatedRelationship_Snapshot_INT_20190131.txt
          -type: StatedRelationship
          -header: [id, effectiveTime, active, moduleId, sourceId, destinationId, relationshipGroup, typeId, characteristicTypeId, modifierId]
          -lines: 1024719
        sct2_Description_Snapshot-en_INT_20190131.txt
          -type: Description
          -header: [id, effectiveTime, active, moduleId, conceptId, languageCode, typeId, term, caseSignificanceId]
          -lines: 1451467
        sct2_Concept_Snapshot_INT_20190131.txt
          -type: Concept
          -header: [id, effectiveTime, active, moduleId, definitionStatusId]
          -lines: 466612
...
```

## Validation

The `rf2 check` command validates each supplied source file and reports problems under the `issues` property of the corresponding file output.

### File Name Validation

RF2 packages are validated against the current [Release Package Naming Convention](https://confluence.ihtsdotools.org/display/DOCRELFMT/3.3.1+Release+Package+Naming+Conventions), while RF2 files are validated against the current [Release File Naming Convention](https://confluence.ihtsdotools.org/display/DOCRELFMT/3.3.2+Release+File+Naming+Convention).  
If a file cannot be recognized by the currently loaded RF2 Specification (see [rf2-spec.yml](spec/index.md)), the `rf2 check` command will report the file as `Unrecognized` type and will not attempt any other validation to run on that file.

### Content Validation

On top of the file name validation, `rf2 check` command provides the ability to validate the actual RF2 content of each RF2 source file. If an RF2 source file is **not** `Unrecognized`, then the tool will search for registered validators and run them on the entirety of the file.

Currently supported content validation rules:

* Not empty validation rule (reports if a column is empty)
* Boolean column type validation rule (reports if a column value is not of type `Boolean`)
* Integer column type validation rule (reports if a column value is not of type `Integer`)
* SNOMED CT Identifier validation rule (reports invalid SNOMED CT IDs, see [SNOMED CT ID](https://confluence.ihtsdotools.org/display/DOCRELFMT/6+SNOMED+CT+Identifiers) specification for more detail)
* Referenced Component ID validation rule (reports if a referenced SNOMED CT ID is invalid or valid but references unexpected SNOMED CT Component, eg. `moduleId` column should refer to a SNOMED CT Concept)
* EffectiveTime validation rule (reports if an `effectiveTime` column value is an incorrect ISO-8601 date value)
* ISO-639 validator for `languageCode` column

### Custom validation rules

This feature is currently in design phase and it is planned to be released at the latest with the v1.0.0 release. 
