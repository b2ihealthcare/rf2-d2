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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import superrf2.Console;
import superrf2.Constants;
import superrf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public abstract class RF2File {

	private final Path path;
	private final RF2FileName fileName;

	public RF2File(Path path) {
		this.path = Objects.requireNonNull(path);
		this.fileName = new RF2FileName(path.getFileName().toString());
	}

	/**
	 * @return the path of this RF2 file
	 */
	public final Path getPath() {
		return path;
	}

	/**
	 * @return the file name of this RF2 file.
	 */
	public final RF2FileName getFileName() {
		return fileName;
	}

	/**
	 * @return <code>true</code> if this RF2 file cannot be recognized as a valid RF2 file up to the currently supported {@link RF2Spec#VERSION RF2 version}.
	 */
	public final boolean isUnrecognized() {
		return this instanceof RF2UnrecognizedFile;
	}

	/**
	 * Prints statistic information about this {@link RF2File} to the given {@link Console} object.
	 * <i>NOTE: overriding methods must invoke super</i>
	 * 
	 * @param console
	 * @throws IOException
	 */
	public void printInfo(Console console) throws IOException {
		console.log("File: %s", getFileName());
//		console.log("Type: %s", getFileName());
//		console.log("Release: %s", getReleaseType());
	}
	
	public static <T extends RF2File> T detect(String path) {
		return detect(Paths.get(path));
	}

	public static <T extends RF2File> T detect(final Path filePath) {
		// first try to detect RF2 file type by its file name 
		RF2File file = detectByFileName(filePath); 
		if (file.isUnrecognized()) {
			// then by the content type aka header
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
