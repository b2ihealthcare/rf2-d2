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
package superrf2.validation;

import superrf2.check.RF2IssueAcceptor;
import superrf2.model.RF2ConceptFile;
import superrf2.model.RF2DescriptionFile;
import superrf2.model.RF2RelationshipFile;
import superrf2.sctIds.Rf2VerhoeffCheck;


/**
 * @since 0.1
 */
public class Rf2IdentifierValidator {
	
	/**
	 * @param componentId - the ID to check
	 * @param acceptor 
	 * @return <code>true</code> if the given componentId is a valid SNOMED CT core component identifier, <code>false</code> otherwise.
	 * @see #validate(String)
	 */
	public static boolean isValid(String componentId, RF2IssueAcceptor acceptor, String componentType) {
		try {
			validate(componentId, acceptor, componentType);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}
	
	private static void validate(final String componentId, RF2IssueAcceptor acceptor, String componentType) throws IllegalArgumentException {
		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			acceptor.error("SCTID should be parseable to a long value");
			throw new IllegalArgumentException();
		}
		
		var idHead = componentId.subSequence(0, componentId.length() - 1);
		var originalChecksum = componentId.charAt(componentId.length() - 1);
		var checksum = Rf2VerhoeffCheck.calculateChecksum(idHead, false);

		if (!Rf2VerhoeffCheck.validateLastChecksumDigit(componentId)) {
			acceptor.error("%s has incorrect Verhoeff check-digit; expected %s, was %s", componentId, checksum, originalChecksum);
			throw new IllegalArgumentException();
		}
		
		var componentIdentifier = getComponentIdentifier(componentId, acceptor);
		
		switch(componentIdentifier) {
		case 0:
			// Concept
			if (!componentType.equals(RF2ConceptFile.COMPONENT_TYPE)) {
				acceptor.error("%s has unsatisfying componentIdentifier: %s for concept component type", componentId, componentIdentifier);
				throw new IllegalArgumentException();
			}
			break;
		case 1:
			// Description
			if (!componentType.equals(RF2DescriptionFile.COMPONENT_TYPE)) {
				acceptor.error("%s has unsatisfying componentIdentifier: %s for description component type", componentId, componentIdentifier);
				throw new IllegalArgumentException();
			}
			break;
		case 2:
			// Relationship
			if (!componentType.equals(RF2RelationshipFile.COMPONENT_TYPE)) {
				acceptor.error("%s has unsatisfying componentIdentifier: %s for relationship component type", componentId, componentIdentifier);
				throw new IllegalArgumentException();
			}
			break;
		default:
			acceptor.error("%s has unkown componentIdentifier: %s for componentType: %s", componentId, componentIdentifier, componentType);
			throw new IllegalArgumentException();
		}
		
	}
	
	private static int getComponentIdentifier(final String componentId, RF2IssueAcceptor acceptor) {
		final char secondPartitionIdDigit = componentId.charAt(componentId.length() - 2);
		final int ci = Character.digit(secondPartitionIdDigit, 10);
		
		if (ci >= 0 && ci <= 2) {
			return ci;
		} else {
			acceptor.error("Second digit of partition identifier must be between '0' and '2', got '%s' for input '%s'.", secondPartitionIdDigit, componentId);
			throw new IllegalArgumentException();
		}
	}

}