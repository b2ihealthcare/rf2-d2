/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.rf2;

import static org.junit.Assert.assertFalse;

import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.junit.Test;

import com.b2international.rf2.model.RF2File;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

/**
 * @since 0.1
 */
public class RF2CheckTest {

	@Test
	public void check_ConceptFile() throws Exception {
//		FileSystem fs = MemoryFileSystemBuilder.newLinux().build();
//		var file = fs.getPath("/sct2_Concept_Delta_INT_20190131.txt");
//		Files.createFile(file);
//		var conceptFile = RF2File.detect(file);
//		assertFalse(conceptFile.isUnrecognized());
	}
	
}
