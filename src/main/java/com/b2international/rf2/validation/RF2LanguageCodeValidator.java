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
package com.b2international.rf2.validation;

import java.util.Locale;
import java.util.Set;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.model.RF2Columns;
import com.b2international.rf2.model.RF2ContentFile;

/**
 * @since 0.1
 */
public final class RF2LanguageCodeValidator implements RF2ColumnValidator {

	private static final Set<String> LANG_CODES = Set.of(Locale.getISOLanguages());
	
	@Override
	public Set<String> getColumns() {
		return Set.of(RF2Columns.LANGUAGE_CODE);
	}

	@Override
	public void check(RF2ContentFile file, String columnHeader, String columnValue, RF2IssueAcceptor acceptor) {
		if (!LANG_CODES.contains(columnValue)) {
			acceptor.error("LanguageCode '%s' is not a valid ISO 639 language code.", columnValue);
		}
	}

}
