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
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.func.RF2FileTransformation;
import com.b2international.rf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public abstract class RF2File {

	public static final String TXT = "txt";
	public static final String TAB = "\t";
	public static final String CRLF = "\r\n";
	
	private final Path parent;
	private final RF2FileName fileName;

	public RF2File(Path parent, RF2FileName fileName) {
		this.parent = Objects.requireNonNull(parent);
		this.fileName = Objects.requireNonNull(fileName);
	}

	/**
	 * @return the path to this RF2 file
	 */
	public final Path getPath() {
		return parent.resolve(getRF2FileName().toString());
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
	public final RF2FileName getRF2FileName() {
		return fileName;
	}

	/**
	 * @return <code>true</code> if this RF2 file cannot be recognized as a valid RF2 file up to the currently supported {@link RF2Spec#RF2_VERSION
	 *         RF2 version}.
	 */
	public final boolean isUnrecognized() {
		return this instanceof RF2UnrecognizedFile || getRF2FileName().isUnrecognized();
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
		getRF2FileName().getUnrecognizedElements().forEach(unrecognized -> {
			acceptor.error("Unrecognized name part: %s", unrecognized);
		});
		getRF2FileName().getMissingElements().forEach(missing -> {
			acceptor.error("Missing name part: %s", missing.getSimpleName().replaceAll("RF2", ""));
		});
	}
	
	/**
	 * Creates this RF2 file at the specified location and file name.
	 * @param context
	 * @throws IOException
	 */
	public abstract void create(RF2CreateContext context) throws IOException;
	
	/**
	 * @return the type (or category) of this {@link RF2File}.
	 */
	public abstract String getType();

	/**
	 * Prepares a script based transformation on this {@link RF2File}.
	 * @param script - the script to use for the transformation
	 * @return an {@link RF2FileTransformation} object
	 */
	public final RF2FileTransformation transform(String script) {
		return new RF2FileTransformation(this, script);
	}

}
