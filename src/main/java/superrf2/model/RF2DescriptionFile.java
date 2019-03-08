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

import superrf2.RF2CreateContext;
import superrf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public final class RF2DescriptionFile extends RF2TerminologyFile {
	
	public RF2DescriptionFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	@Override
	protected String[] getRF2HeaderSpec() {
		return new String[] {
			RF2Columns.ID,
			RF2Columns.EFFECTIVE_TIME,
			RF2Columns.ACTIVE,
			RF2Columns.MODULE_ID,
			RF2Columns.CONCEPT_ID,
			RF2Columns.LANGUAGE_CODE,
			RF2Columns.TYPE_ID,
			RF2Columns.TERM,
			RF2Columns.CASE_SIGNIFICANCE_ID
		};
	}
	
	public static RF2DescriptionFile create(Path parent, String contentSubType, RF2CreateContext context) {
		final String fileName = String.format("sct2_Description_%s-en-GB_%s%s_%s.%s", contentSubType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2DescriptionFile(parent, new RF2FileName(fileName));
	}

}
