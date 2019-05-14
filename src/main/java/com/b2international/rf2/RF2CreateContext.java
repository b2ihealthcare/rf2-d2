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
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.b2international.rf2.console.Console;
import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1
 */
public final class RF2CreateContext extends RF2Context{

	private final List<RF2File> sources;

	public RF2CreateContext(RF2Specification specification, List<RF2File> sources, Console log) {
		super(specification, log);
		this.sources = sources == null ? Collections.emptyList() : sources;
	}
	
	public List<RF2File> getSources() {
		return sources;
	}

	public void visitSourceRows(Predicate<RF2ContentFile> fileFilter, Predicate<String[]> lineFilter, boolean parallel, BiConsumer<RF2File, String[]> visitor) throws IOException {
		for (RF2File source : sources) {
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
							.forEach(line -> visitor.accept(contentFile, line));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	
}
