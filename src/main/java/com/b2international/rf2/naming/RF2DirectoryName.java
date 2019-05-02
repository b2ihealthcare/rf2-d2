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
package com.b2international.rf2.naming;

import java.nio.file.Path;

import com.b2international.rf2.model.RF2Directory;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 
 */
public final class RF2DirectoryName extends RF2FileName {

	public RF2DirectoryName(String fileName) {
		super(fileName, RF2NameElement.AcceptAll.class);
	}

	@Override
	public boolean isUnrecognized() {
		return false;
	}

	@Override
	public RF2Directory createRF2File(Path parent, RF2Specification specification) {
		return new RF2Directory(parent, this, specification);
	}

}
