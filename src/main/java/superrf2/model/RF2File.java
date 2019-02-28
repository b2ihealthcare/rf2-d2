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
import superrf2.naming.RF2FileName;
import superrf2.naming.RF2FileNameBase;
import superrf2.naming.RF2ReleaseName;

/**
 * @since 0.1
 */
public abstract class RF2File {

	private final Path parent;
	private final RF2FileNameBase fileName;

	public RF2File(Path parent, RF2FileNameBase fileName) {
		this.parent = Objects.requireNonNull(parent);
		this.fileName = Objects.requireNonNull(fileName);
	}

	/**
	 * @return the path to this RF2 file
	 */
	public final Path getPath() {
		return parent.resolve(getFileName().toString());
	}
	
	/**
	 * @return the parent path of this RF2 file
	 */
	public final Path getParent() {
		return parent;
	}

	/**
	 * @return the file name of this RF2 file.
	 */
	public final RF2FileNameBase getFileName() {
		return fileName;
	}

	/**
	 * @return <code>true</code> if this RF2 file cannot be recognized as a valid RF2 file up to the currently supported {@link RF2Spec#VERSION RF2 version}.
	 */
	public final boolean isUnrecognized() {
		return this instanceof RF2UnrecognizedFile || getFileName().isUnrecognized();
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
	
	public static <T extends RF2File> T detect(final String path) {
		return detect(Paths.get(path));
	}

	public static <T extends RF2File> T detect(final Path path) {
		String fileName = path.getFileName().toString();
		// first try to parse the fileName path as RF2Release
		RF2FileNameBase rf2Release = new RF2ReleaseName(fileName);
		if (rf2Release.isUnrecognized()) {
			// otherwise fall back and treat it as an RF2 File (in general any file can be part of a release)
			rf2Release = new RF2FileName(fileName);
		}
		return (T) rf2Release.createRF2File(path);
	}
	
}
