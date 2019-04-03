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

import java.nio.file.Path;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.naming.RF2FileName;

/**
 * @since 0.2.0
 */
public final class RF2IdentifierFile extends RF2ContentFile {

	public RF2IdentifierFile(Path parent, RF2FileName fileName) {
		super(parent, fileName);
	}

	@Override
	protected String[] getRF2HeaderSpec() {
		return new String[] {
			RF2Columns.IDENTIFIER_SCHEME_ID,
			RF2Columns.ALTERNATE_IDENTIFIER,
			RF2Columns.EFFECTIVE_TIME,
			RF2Columns.ACTIVE,
			RF2Columns.MODULE_ID,
			RF2Columns.REFERENCED_COMPONENT_ID
		};
	}

	public static RF2IdentifierFile create(Path parent, String contentType, RF2CreateContext context) {
		final String fileName = String.format("sct2_Identifier_%s_%s%s_%s.%s", contentType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2IdentifierFile(parent, new RF2FileName(fileName));
	}
	
}
