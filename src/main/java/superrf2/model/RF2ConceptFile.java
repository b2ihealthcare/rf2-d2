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

import java.nio.file.Path;
import java.util.stream.Stream;

import superrf2.RF2CreateContext;
import superrf2.check.RF2IssueAcceptor;
import superrf2.naming.RF2FileName;
import superrf2.validation.Rf2IdentifierValidator;

/**
 * @since 0.1
 */
public final class RF2ConceptFile extends RF2ContentFile {
	
	public static final String COMPONENT_TYPE = "Concept";
	
	public RF2ConceptFile(Path parent, RF2FileName fileName) {
		super(parent, fileName);
	}
	
	@Override
	protected String[] getRF2HeaderSpec() {
		return new String[] {
			RF2Columns.ID,
			RF2Columns.EFFECTIVE_TIME,
			RF2Columns.ACTIVE,
			RF2Columns.MODULE_ID,
			RF2Columns.DEFINITION_STATUS_ID
		};
	}
	
	@Override
	protected void validateRows(RF2IssueAcceptor acceptor, Stream<String[]> rows) {
		rows.forEach(row -> {
			var conceptId = row[0];
			if (!Rf2IdentifierValidator.isValid(conceptId, acceptor, COMPONENT_TYPE)) {
				acceptor.error("Concept id is not a valid identifier: %s", conceptId);
			}
		});
	}
	
	public static RF2ConceptFile create(Path parent, String contentType, RF2CreateContext context) {
		final String fileName = String.format("sct2_Concept_%s_%s%s_%s.%s", contentType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2ConceptFile(parent, new RF2FileName(fileName));
	}

}
