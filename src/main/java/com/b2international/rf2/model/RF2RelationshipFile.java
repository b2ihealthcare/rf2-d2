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
import com.b2international.rf2.naming.file.RF2ContentType;

/**
 * @since 0.1
 */
public final class RF2RelationshipFile extends RF2ContentFile {

	public RF2RelationshipFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	@Override
	protected String[] getRF2HeaderSpec() {
		return new String[] {
			RF2Columns.ID,
			RF2Columns.EFFECTIVE_TIME,
			RF2Columns.ACTIVE,
			RF2Columns.MODULE_ID,
			RF2Columns.SOURCE_ID,
			RF2Columns.DESTINATION_ID,
			RF2Columns.RELATIONSHIP_GROUP,
			RF2Columns.TYPE_ID,
			RF2Columns.CHARACTERISTIC_TYPE_ID,
			RF2Columns.MODIFIER_ID,
		};
	}

	public static RF2RelationshipFile create(Path parent, String contentSubType, RF2CreateContext context) {
		final String fileName = String.format("sct2_%s_%s_%s%s_%s.%s", RF2ContentType.RELATIONSHIP, contentSubType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2RelationshipFile(parent, new RF2FileName(fileName));
	}
	
	public static RF2RelationshipFile createStated(Path parent, String contentSubType, RF2CreateContext context) {
		final String fileName = String.format("sct2_%s_%s_%s%s_%s.%s", RF2ContentType.STATED_RELATIONSHIP, contentSubType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2RelationshipFile(parent, new RF2FileName(fileName));
	}
	
}
