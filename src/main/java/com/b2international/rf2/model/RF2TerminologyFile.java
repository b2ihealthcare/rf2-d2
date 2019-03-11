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
package com.b2international.rf2.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.validation.RF2ColumnValidator;

/**
 * @since 0.1
 */
public abstract class RF2TerminologyFile extends RF2ContentFile {

	public RF2TerminologyFile(Path parent, RF2FileName fileName) {
		super(parent, fileName);
	}

	@Override
	protected void checkContent(RF2IssueAcceptor acceptor) throws IOException {
		// assign validators to RF2 columns
		final String[] header = getHeader();
		final Map<Integer, RF2ColumnValidator> validatorsByIndex = new HashMap<>(header.length);
		for (int i = 0; i < header.length; i++) {
			final String columnHeader = header[i];
			final RF2ColumnValidator validator = RF2ColumnValidator.VALIDATORS.get(columnHeader);
			if (validator != null) {
				validatorsByIndex.put(i, validator);
			} else {
				validatorsByIndex.put(i, RF2ColumnValidator.NOOP);
				acceptor.warn("No validator is registered for column header '%s'.", columnHeader);
			}
		}
		
		rows().forEach(row -> {
			for (int i = 0; i < row.length; i++) {
				validatorsByIndex.get(i).check(this, row[i], acceptor);
			}
		});
	}
	
}
