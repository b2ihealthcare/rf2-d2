/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sggetComponentCategory
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
import java.util.Optional;
import java.util.Set;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.model.RF2Columns;
import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.sctid.RF2VerhoeffCheck;


/**
 * @since 0.1
 */
public final class RF2SCTIDValidator implements RF2ColumnValidator {
	
	private static final Map<String, Integer> COMPONENT_TYPES_DIGIT_ASSOCIATION = Map.of(
		RF2ContentType.CONCEPT.getContentType(), 0,
		RF2ContentType.DESCRIPTION.getContentType(), 1,
		RF2ContentType.TEXT_DEFINITION.getContentType(), 1,
		RF2ContentType.RELATIONSHIP.getContentType(), 2,
		RF2ContentType.STATED_RELATIONSHIP.getContentType(), 2
	);
	
	@Override
	public Set<String> getColumns() {
		return Set.of(RF2Columns.ID);
	}

	@Override
	public void check(RF2ContentFile file, String columnValue, RF2IssueAcceptor acceptor) {
		// verify that the RF2 file do have content type in the file name otherwise
		Optional<RF2ContentType> rf2ContentType = file.getFileName().getElement(RF2ContentType.class);
		if (!rf2ContentType.isPresent()) {
			acceptor.warn("Unable to validate SCTID column due to missing content type part in file name");
			return;
		}
		
		if (columnValue == null || columnValue.isBlank()) {
			acceptor.error("SCTID '%s' is empty or contains only white space characters.", columnValue);
		}
		
		// validate that it is a number
		try {
			Long.parseLong(columnValue);
		} catch (final NumberFormatException e) {
			acceptor.error("SCTID '%s' should be a number.", columnValue);
		}
		
		// validate leading zero
		if (columnValue.startsWith("0")) {
			acceptor.error("SCTID '%s' can't start with leading zero.", columnValue);
		}
		
		// validate number of digits between 6-18
		if (columnValue.length() < 6 || columnValue.length() > 18) {
			acceptor.error("SCTID '%s' length must be between 6-18 characters.", columnValue);
		}

		// validate component identifier in partition identifier
		var actualComponentIdentifier = getComponentIdentifier(columnValue, acceptor);
		var expectedComponentIdentifier = COMPONENT_TYPES_DIGIT_ASSOCIATION.get(rf2ContentType.get().getContentType());
		
		if (actualComponentIdentifier != expectedComponentIdentifier) {
			acceptor.error("SCTID '%s' has unsatisfying componentIdentifier. Expected '%s' but was '%s'.", columnValue, expectedComponentIdentifier, actualComponentIdentifier);
		}
		
		// validate Verhoeff check digit
		var rawComponentId = columnValue.subSequence(0, columnValue.length() - 1);
		var expectedChecksum = RF2VerhoeffCheck.calculateChecksum(rawComponentId, false);
		var actualChecksum = columnValue.charAt(columnValue.length() - 1);
		if (actualChecksum != expectedChecksum) {
			acceptor.error("SCTID '%s' has incorrect Verhoeff check-digit. Expected '%s' but was '%s'.", columnValue, expectedChecksum, actualChecksum);
		}
	}
	
	private static int getComponentIdentifier(final String componentId, RF2IssueAcceptor acceptor) {
		final char secondPartitionIdDigit = componentId.charAt(componentId.length() - 2);
		return Character.digit(secondPartitionIdDigit, 10);
	}

}