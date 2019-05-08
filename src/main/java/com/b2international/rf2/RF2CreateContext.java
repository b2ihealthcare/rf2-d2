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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1
 */
public final class RF2CreateContext {

	private final RF2Specification specification;
	private final List<RF2File> sources;
	private final Console log;
	
	public RF2CreateContext(RF2Specification specification, List<RF2File> sources, Console log) {
		this.specification = specification;
		this.log = log;
		this.sources = sources == null ? Collections.emptyList() : sources;
	}
	
	public RF2Specification getSpecification() {
		return specification;
	}
	
	public List<RF2File> getSources() {
		return sources;
	}
	
	public void log(String message, Object... args) {
		log.log(message, args);
	}

	public void warn(String message, Object... args) {
		log.warn(message, args);
	}

	public void error(String message, Object... args) {
		log.error(message, args);
	}
	
	public void visitSourceRows(Predicate<RF2ContentFile> fileFilter, Predicate<String[]> lineFilter, boolean parallel, Consumer<String[]> visitor) throws IOException {
		for (RF2File source : getSources()) {
			source.visit(file -> {
				if (file instanceof RF2ContentFile) {
					final RF2ContentFile contentFile = (RF2ContentFile) file;
					if (!fileFilter.test(contentFile)) {
						return;
					}
					
					try {
						// read lines
						Stream<String[]> rows = parallel ? contentFile.rowsParallel() : contentFile.rows();
						rows
							.filter(lineFilter)
							.forEach(visitor::accept);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	
}
