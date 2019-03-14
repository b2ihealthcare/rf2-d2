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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.naming.RF2DirectoryName;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.RF2FileNameBase;
import com.b2international.rf2.naming.RF2ReleaseName;

/**
 * @since 0.1
 */
public abstract class RF2File {

	public static final String TXT = "txt";
	public static final String TAB = "\t";
	public static final String CRLF = "\r\n";
	
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
	 * @return <code>true</code> if this RF2 file cannot be recognized as a valid RF2 file up to the currently supported {@link RF2Spec#RF2_VERSION
	 *         RF2 version}.
	 */
	public final boolean isUnrecognized() {
		return this instanceof RF2UnrecognizedFile || getFileName().isUnrecognized();
	}

	/**
	 * Visit this {@link RF2File} with the given visitor.
	 * 
	 * @param visitor
	 * @throws IOException 
	 */
	public abstract void visit(Consumer<RF2File> visitor) throws IOException;

	/**
	 * Check that this RF2File conforms to the RF2 specification and reports warnings and errors if not.
	 * @param acceptor
	 * @throws IOException 
	 */
	public void check(RF2IssueAcceptor acceptor) throws IOException {
		// check name first
		getFileName().getUnrecognizedElements().forEach(unrecognized -> {
			acceptor.error("Unrecognized name part: %s", unrecognized);
		});
		getFileName().getMissingElements().forEach(missing -> {
			acceptor.error("Missing name part: %s", missing.getSimpleName().replaceAll("RF2", ""));
		});
	}
	
	/**
	 * Creates the RF2 file at the specified location and file name.
	 * @param context
	 * @throws IOException
	 */
	public abstract void create(RF2CreateContext context) throws IOException;
	
	/**
	 * @return the type (or category) of this {@link RF2File}.
	 */
	public abstract String getType();

	/**
	 * Detects an {@link RF2File} from the given file path.
	 * 
	 * @param path
	 *            - the file path to recognize
	 * @return an {@link RF2File} instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends RF2File> T detect(final Path path) {
		if (!Files.exists(path)) {
			throw new IllegalArgumentException(String.format("'%s' path argument does not exist.", path));
		}
		
		String fileName = path.getFileName().toString();
		RF2FileNameBase rf2Release;
		// directories are always recognized and accepted
		if (Files.isDirectory(path)) {
			rf2Release = new RF2DirectoryName(fileName);
		} else {
			// if we are not in the OS file system, then in case of zip files we treat them as regular files
			if (path.toUri().getScheme().equals("jar")) {
				rf2Release = new RF2FileName(fileName);
			} else {
				// if it is not a directory and we are in the OS file system then try to parse the fileName as an RF2Release
				rf2Release = new RF2ReleaseName(fileName);
				if (rf2Release.isUnrecognized()) {
					// if it is not a release package fall back and treat it as an RF2 File (in general any file can be part of a release)
					rf2Release = new RF2FileName(fileName);
				}
			}
		}
		return (T) rf2Release.createRF2File(path.getParent());
	}

}
