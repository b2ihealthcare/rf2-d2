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
import java.nio.file.Paths;

import superrf2.Constants;

/**
 * @since 0.1
 */
public final class RF2Format {

	public static final String VERSION = "20190131";

	public static <T extends RF2File> T detect(String path) {
		return detect(Paths.get(path));
	}

	public static <T extends RF2File> T detect(final Path filePath) {
		// first try to detect RF2 file type by its file name 
		RF2File file = detectByFileName(filePath); 
		if (file.isUnrecognized()) {
			file = detectByContent(filePath);
		}
		return (T) file;
	}
	
	private static RF2File detectByFileName(Path path) {
		String fileName = path.getFileName().toString();
		if (fileName.startsWith("sct2_Concept")) {
			return new RF2ConceptFile(path);
		} else if (fileName.endsWith(Constants.ZIP)) {
			return new RF2Bundle(path);
		}
		return new RF2UnrecognizedFile(path);
	}
	
	private static RF2File detectByContent(Path path) {
		return new RF2UnrecognizedFile(path);
	}
	
}
