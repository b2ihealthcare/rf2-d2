/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package superrf2.model;

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
	
	// Refset fields
	public static final String REFSET_ID = "refsetId";
	public static final String REFERENCED_COMPONENT_ID = "referencedComponentId";
	
	private RF2Columns() {}
	
}
