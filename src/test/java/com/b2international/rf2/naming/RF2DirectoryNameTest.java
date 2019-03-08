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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.rf2.naming.RF2DirectoryName;
import com.b2international.rf2.naming.RF2NameElement;

/**
 * @since 0.1
 */
public class RF2DirectoryNameTest {

	@Test
	public void acceptAll() throws Exception {
		RF2DirectoryName rf2FileName = new RF2DirectoryName("acceptAllDirectoryNames");
		assertTrue(rf2FileName.getExtension().isEmpty());
		assertThat(rf2FileName.getElements())
			.contains(new RF2NameElement.AcceptAll("acceptAllDirectoryNames"));
		assertFalse(rf2FileName.hasUnrecognizedElement());
	}
	
}
