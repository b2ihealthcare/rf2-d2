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

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.sctid.RF2VerhoeffCheck;


/**
 * @since 0.1
 */
public final class RF2IdentifierValidator {
	
	private RF2IdentifierValidator() {}
	
	private static final Map<String, Integer> COMPONENT_TYPES_DIGIT_ASSOCIATION = Map.of(
		RF2ContentType.CONCEPT.getContentType(), 0,
		RF2ContentType.DESCRIPTION.getContentType(), 1,
		RF2ContentType.TEXT_DEFINITION.getContentType(), 1,
		RF2ContentType.RELATIONSHIP.getContentType(), 2,
		RF2ContentType.STATED_RELATIONSHIP.getContentType(), 2
	);
	
	/**
	 * @param componentId - the ID to check
	 * @param expectedComponentType - the expected component type
	 * @param acceptor - to report issues
	 * @return <code>true</code> if the given componentId is a valid SNOMED CT core component identifier, <code>false</code> otherwise.
	 */
	public static boolean isValid(String componentId, String expectedComponentType, RF2IssueAcceptor acceptor) {
		if (componentId == null || componentId.isBlank()) {
			acceptor.error("SCTID '%s' is empty or contains only white space characters.", componentId);
			return false;
		}
		
		// validate that it is a number
		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			acceptor.error("SCTID '%s' should be a number.", componentId);
			return false;
		}
		
		// validate leading zero
		if (componentId.startsWith("0")) {
			acceptor.error("SCTID '%s' can't start with leading zero.", componentId);
			return false;
		}
		
		// validate number of digits between 6-18
		if (componentId.length() < 6 || componentId.length() > 18) {
			acceptor.error("SCTID '%s' length must be between 6-18 characters.", componentId);
			return false;
		}

		// validate component identifier in partition identifier
		var actualComponentIdentifier = getComponentIdentifier(componentId, acceptor);
		var expectedComponentIdentifier = COMPONENT_TYPES_DIGIT_ASSOCIATION.get(expectedComponentType);
		
		if (actualComponentIdentifier != expectedComponentIdentifier) {
			acceptor.error("SCTID '%s' has unsatisfying componentIdentifier. Expected '%s' but was '%s'.", componentId, expectedComponentIdentifier, actualComponentIdentifier);
			return false;
		}
		
		// validate Verhoeff check digit
		var rawComponentId = componentId.subSequence(0, componentId.length() - 1);
		var expectedChecksum = RF2VerhoeffCheck.calculateChecksum(rawComponentId, false);
		var actualChecksum = componentId.charAt(componentId.length() - 1);
		if (actualChecksum != expectedChecksum) {
			acceptor.error("%s has incorrect Verhoeff check-digit. Expected '%s' but was '%s'.", componentId, expectedChecksum, actualChecksum);
			return false;
		}
		
		return true;
	}
	
	private static int getComponentIdentifier(final String componentId, RF2IssueAcceptor acceptor) {
		final char secondPartitionIdDigit = componentId.charAt(componentId.length() - 2);
		return Character.digit(secondPartitionIdDigit, 10);
	}

}