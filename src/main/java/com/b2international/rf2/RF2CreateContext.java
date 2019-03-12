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
package com.b2international.rf2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;

/**
 * @since 0.1
 */
public final class RF2CreateContext {

	private final String[] contentSubTypes;
	private final String releaseDate;
	private final String country;
	private final String namespace;
	private final List<RF2File> sources;
	private final Console log;
	
	public RF2CreateContext(
			String[] contentSubTypes, 
			String releaseDate, 
			String country, 
			String namespace, 
			List<RF2File> sources, 
			Console log) {
		this.contentSubTypes = contentSubTypes;
		this.log = log;
		this.releaseDate = Objects.requireNonNull(releaseDate);
		this.country = Objects.requireNonNull(country);
		this.namespace = Objects.requireNonNull(namespace);
		this.sources = sources == null ? Collections.emptyList() : sources;
	}
	
	public String[] getContentSubTypes() {
		return contentSubTypes;
	}
	
	public String getCountry() {
		return country;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}
	
	public List<RF2File> getSources() {
		return sources;
	}
	
	public Console log() {
		return log;
	}
	
	public void visitSourceRows(String expectedContentType, String[] expectedHeader, boolean parallel, Consumer<String[]> visitor) throws IOException {
		for (RF2File source : getSources()) {
			source.visit(file -> {
				final String actualContentType = file.getType();
				if (!actualContentType.equals(expectedContentType)) {
					return;
				}
				
				if (file instanceof RF2ContentFile) {
					try {
						// check actual content type as well, to copy content from the right files
						if (!Arrays.equals(((RF2ContentFile) file).getHeader(), expectedHeader)) {
							return;
						}
						// read lines
						Stream<String[]> rows = parallel ? ((RF2ContentFile) file).rowsParallel() : ((RF2ContentFile) file).rows();
						for (String[] line : (Iterable<String[]>) rows::iterator) {
							visitor.accept(line);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}

}
