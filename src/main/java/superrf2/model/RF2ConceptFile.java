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

import superrf2.naming.RF2FileName;

/**
 * @since 0.1
 */
@RF2Header({
	RF2Columns.ID,
	RF2Columns.EFFECTIVE_TIME,
	RF2Columns.ACTIVE,
	RF2Columns.MODULE_ID,
	RF2Columns.DEFINITION_STATUS_ID
})
public final class RF2ConceptFile extends RF2ContentFile {

	public RF2ConceptFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
}
