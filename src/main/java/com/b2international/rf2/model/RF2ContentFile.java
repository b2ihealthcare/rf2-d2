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
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.file.RF2ContentType;

/**
 * @since 0.1
 */
public abstract class RF2ContentFile extends RF2File {

	private String[] header;
	
	public RF2ContentFile(Path parent, RF2FileName fileName) {
		super(parent, fileName);
	}
	
	@Override
	public String getType() {
		return getFileName()
				.getElement(RF2ContentType.class)
				.map(RF2ContentType::getContentType)
				// TODO recognize type from header
				.orElse("Unknown");
	}
	
	@Override
	public void visit(Consumer<RF2File> visitor) {
		visitor.accept(this);
	}
	
	@Override
	public void check(RF2IssueAcceptor acceptor) throws IOException {
		super.check(acceptor);
		// check RF2 header
		final String[] rf2HeaderSpec = getRF2HeaderSpec();
		final String[] actualHeader = getHeader();
		if (!Arrays.equals(rf2HeaderSpec, actualHeader)) {
			// TODO report incorrect header columns
			acceptor.error("Header does not conform to specification");
		}
		
		checkContent(acceptor);
	}
	
	protected abstract void checkContent(RF2IssueAcceptor acceptor) throws IOException;

	@Override
	public void create(RF2CreateContext context) throws IOException {
		Files.writeString(getPath(), newLine(getHeader()), StandardOpenOption.CREATE_NEW);
	}
	
	protected final String newLine(String[] values) {
		return String.format("%s%s", String.join(TAB, values), CRLF);
	}

	/**
	 * @return the current RF2 header by reading the first line of the file or if this is a non-existing file returns the header from the spec for kind of RF2 files
	 * @throws IOException 
	 */
	public final String[] getHeader() throws IOException {
		if (header == null) {
			if (Files.exists(getPath())) {
				header = Files.lines(getPath()).findFirst().orElse("N/A").split(TAB);
			} else {
				header = getRF2HeaderSpec();
			}
		}
		return header;
	}

	/**
	 * @return the RF2 specification header from the {@link RF2Header} l
	 */
	protected abstract String[] getRF2HeaderSpec();
	
	/**
	 * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects
	 * @throws IOException
	 */
	public final Stream<String[]> rows() throws IOException {
		return Files.lines(getPath())
				.skip(1)
				.map(line -> line.split(TAB));
	}

}
