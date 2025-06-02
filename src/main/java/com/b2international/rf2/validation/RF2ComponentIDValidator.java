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
package com.b2international.rf2.validation;

import java.util.Map;
import java.util.Set;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.model.RF2Columns;
import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.naming.file.RF2ContentType;

/**
 * Validates columns that should have a non-empty value that represents a valid SNOMED CT component identifier.
 * 
 * @since 0.1
 */
public final class RF2ComponentIDValidator implements RF2ColumnValidator {

	private static final Map<String, Set<RF2ContentType>> COLUMNS_TO_TYPES = Map.ofEntries(
		// Concept file
		Map.entry(RF2Columns.MODULE_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.DEFINITION_STATUS_ID, Set.of(RF2ContentType.CONCEPT)),
		// Description file
		Map.entry(RF2Columns.CONCEPT_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.TYPE_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.CASE_SIGNIFICANCE_ID, Set.of(RF2ContentType.CONCEPT)),
		// Relationship file
		Map.entry(RF2Columns.SOURCE_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.DESTINATION_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.CHARACTERISTIC_TYPE_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.MODIFIER_ID, Set.of(RF2ContentType.CONCEPT)),
		// Reference Set files
		Map.entry(RF2Columns.REFSET_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.ACCEPTABILITY_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.VALUE_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.MAP_CATEGORY_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.DESCRIPTION_FORMAT, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.MRCM_RULE_REFSET_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.REFERENCED_COMPONENT_ID, Set.of(RF2ContentType.CONCEPT, RF2ContentType.DESCRIPTION, RF2ContentType.RELATIONSHIP)),
		Map.entry(RF2Columns.TARGET_COMPONENT_ID, Set.of(RF2ContentType.CONCEPT, RF2ContentType.DESCRIPTION, RF2ContentType.RELATIONSHIP)),
		Map.entry(RF2Columns.CORRELATION_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.DOMAIN_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.RULE_STRENGTH_ID, Set.of(RF2ContentType.CONCEPT)),
		Map.entry(RF2Columns.CONTENT_TYPE_ID, Set.of(RF2ContentType.CONCEPT))
	);
	
	@Override
	public Set<String> getColumns() {
		return COLUMNS_TO_TYPES.keySet();
	}
	
	@Override
	public void check(RF2ContentFile file, String columnHeader, String columnValue, RF2IssueAcceptor acceptor) {
		// validate that the module field has a valid concept ID
		RF2IdentifierValidator.checkSCTID(COLUMNS_TO_TYPES.get(columnHeader), columnValue, acceptor);
	}

}
