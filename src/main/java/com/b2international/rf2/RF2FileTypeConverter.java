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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine.ITypeConverter;

/**
 * @since 0.3.2
 */
final class RF2FileTypeConverter implements ITypeConverter<RF2File> {

	@Override
	public RF2File convert(String path) throws Exception {
		RF2Specification spec = RF2Command.getRF2Specification();
		Path rf2FilePath = Paths.get(path);
		if (Files.exists(rf2FilePath)) {
			return spec.detect(rf2FilePath);
		}
		
		rf2FilePath = RF2Command.WORK_DIR.resolve(path);
		
		if (Files.exists(rf2FilePath)) {
			return spec.detect(rf2FilePath);
		}
		
		throw new IllegalArgumentException(String.format("File or path could not be resolved: '%s'", path));
	}

}
