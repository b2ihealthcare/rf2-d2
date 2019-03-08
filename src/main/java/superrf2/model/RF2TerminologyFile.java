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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import superrf2.check.RF2IssueAcceptor;
import superrf2.naming.RF2FileName;
import superrf2.naming.file.RF2ContentType;
import superrf2.validation.RF2IdentifierValidator;

/**
 * @since 0.1
 */
public abstract class RF2TerminologyFile extends RF2ContentFile {

	public RF2TerminologyFile(Path parent, RF2FileName fileName) {
		super(parent, fileName);
	}

	@Override
	protected void checkContent(RF2IssueAcceptor acceptor) throws IOException {
		Optional<RF2ContentType> rf2ContentType = getFileName().getElement(RF2ContentType.class);
		if (rf2ContentType.isPresent()) {
			rows().forEach(row -> {
				var componentId = row[0];
				final String contentType = rf2ContentType.get().getContentType();
				if (!RF2IdentifierValidator.isValid(componentId, contentType, acceptor)) {
					acceptor.error("%s id is not a valid identifier: %s", contentType, componentId);
				}
			});	
		}
	}
	
}
