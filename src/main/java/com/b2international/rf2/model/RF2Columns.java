/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.rf2.model;

/**
 * @since 0.1
 */
public abstract class RF2Columns {

	// Common fields
	public static final String ID = "id";
	public static final String EFFECTIVE_TIME = "effectiveTime";
	public static final String ACTIVE = "active";
	public static final String MODULE_ID = "moduleId";
	
	// Concept fields
	public static final String DEFINITION_STATUS_ID = "definitionStatusId";
	
	// Description fields
	public static final String CONCEPT_ID = "conceptId";
	public static final String LANGUAGE_CODE = "languageCode";
	public static final String TYPE_ID = "typeId";
	public static final String TERM = "term";
	public static final String CASE_SIGNIFICANCE_ID = "caseSignificanceId";
	
	// Relationship fields
	public static final String SOURCE_ID = "sourceId";
	public static final String DESTINATION_ID = "destinationId";
	public static final String RELATIONSHIP_GROUP = "relationshipGroup";
	public static final String CHARACTERISTIC_TYPE_ID = "characteristicTypeId";
	public static final String MODIFIER_ID = "modifierId";
	
	// Identifier
	public static final String IDENTIFIER_SCHEME_ID = "identifierSchemeId";
	public static final String ALTERNATE_IDENTIFIER = "alternateIdentifier";
	
	// Refset fields
	public static final String REFSET_ID = "refsetId";
	public static final String REFERENCED_COMPONENT_ID = "referencedComponentId";
	
	// Ordered
	public static final String ORDER = "order";
	
	// Attribute
	public static final String VALUE_ID = "valueId";
	
	// Language
	public static final String ACCEPTABILITY_ID = "acceptabilityId";
	
	// Association
	public static final String TARGET_COMPONENT_ID = "targetComponentId";
	
	// Annotation
	public static final String ANNOTATION = "annotation";

	// Query
	public static final String QUERY = "query";
	
	// Extended Map
	public static final String MAP_CATEGORY_ID = "mapCategoryId";
	
	// Complex Map
	public static final String MAP_GROUP = "mapGroup";
	public static final String MAP_PRIORITY = "mapPriority";
	public static final String MAP_RULE = "mapRule";
	public static final String MAP_ADVICE = "mapAdvice";
	public static final String CORRELATION_ID = "correlationId";
	
	// Simple Map
	public static final String MAP_TARGET = "mapTarget";
	
	// RefSet Descriptor
	public static final String ATTRIBUTE_DESCRIPTION = "attributeDescription";
	public static final String ATTRIBUTE_TYPE = "attributeType";
	public static final String ATTRIBUTE_ORDER = "attributeOrder";
	
	// Module Dependency
	public static final String SOURCE_EFFECTIVE_TIME = "sourceEffectiveTime";
	public static final String TARGET_EFFECTIVE_TIME = "targetEffectiveTime";
	
	// Description format
	public static final String DESCRIPTION_FORMAT = "descriptionFormat";
	public static final String DESCRIPTION_LENGTH = "descriptionLength";
	
	// Map Correlation and Origin
	public static final String ATTRIBUTE_ID = "attributeId";
	public static final String CONTENT_ORIGIN_ID = "contentOriginId";
	
	// MRCM Domain
	public static final String DOMAIN_CONSTRAINT = "domainConstraint";
	public static final String PARENT_DOMAIN = "parentDomain";
	public static final String PROXIMAL_PRIMITIVE_CONSTRAINT = "proximalPrimitiveConstraint";
	public static final String PROXIMAL_PRIMITIVE_REFINEMENT = "proximalPrimitiveRefinement";
	public static final String DOMAIN_TEMPLATE_FOR_PRECOORDINATION = "domainTemplateForPrecoordination";
	public static final String DOMAIN_TEMPLATE_FOR_POSTCOORDINATION = "domainTemplateForPostcoordination";
	public static final String GUIDE_URL = "guideURL";
	
	// MRCM Attribut Domain
	public static final String DOMAIN_ID = "domainId";
	public static final String GROUPED = "grouped";
	public static final String ATTRIBUTE_CARDINALITY = "attributeCardinality";
	public static final String ATTRIBUTE_IN_GROUP_CARDINALITY = "attributeInGroupCardinality";
	public static final String RULE_STRENGTH_ID = "ruleStrengthId";
	public static final String CONTENT_TYPE_ID = "contentTypeId";
	
	// MRCM Attribute Range
	public static final String RANGE_CONSTRAINT = "rangeConstraint";
	public static final String ATTRIBUTE_RULE = "attributeRule";
	
	// MRCM Module Scope
	public static final String MRCM_RULE_REFSET_ID = "mrcmRuleRefsetId";
	
	// OWL Expression
	public static final String OWL_EXPRESSION = "owlExpression";
	
	private RF2Columns() {}
	
}
