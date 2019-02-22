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
import java.util.Objects;

import superrf2.Console;

/**
 * @since 0.1
 */
public abstract class RF2File {

	private final Path path;
	private final String fileName;
	private final RF2ReleaseType releaseType;
	private final RF2FileType fileType;

	public RF2File(Path path) {
		this.path = Objects.requireNonNull(path);
		this.fileName = path.getFileName().toString();
		this.fileType = RF2FileType.SCT;
		this.releaseType = RF2ReleaseType.FULL;
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
	public final String getFileName() {
		return fileName;
	}

	/**
	 * @return the {@link RF2FileType} for this kind of RF2 files.
	 */
	public final RF2FileType getFileType() {
		return fileType;
	}
	
	/**
	 * @return the {@link RF2ReleaseType} currently specified for this file.
	 */
	public final RF2ReleaseType getReleaseType() {
		return releaseType;
	}
	
	/**
	 * @return <code>true</code> if this RF2 file cannot be recognized as a valid RF2 file up to the currently supported {@link RF2Format#VERSION RF2 version}.
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
		console.log("Type: %s", getFileType());
		console.log("Release: %s", getReleaseType());
	}

}
