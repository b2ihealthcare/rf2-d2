/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.rf2.naming;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.model.RF2UnrecognizedFile;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2CountryNamespace;
import com.b2international.rf2.naming.file.RF2FileType;
import com.b2international.rf2.naming.file.RF2VersionDate;
import com.b2international.rf2.spec.RF2ContentFileSpecification;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1
 */
public final class RF2ContentFileName extends RF2FileName {

	public RF2ContentFileName(String fileName) {
		super(fileName, 
			RF2FileType.class,
			RF2ContentType.class,
			RF2ContentSubType.class,
			RF2CountryNamespace.class,
			RF2VersionDate.class
		);
	}

	@Override
	public boolean isUnrecognized() {
		return getElements().isEmpty();
	}
	
	/**
	 * Creates an {@link RF2ContentFile} based on the described RF2 File Name elements and/or content type aka header.
	 * 
	 * @param parent - the parent path where the file is currently located or will be located 
	 * @return an {@link RF2ContentFile} instance or an {@link RF2UnrecognizedFile} instance if this file cannot be recognized as valid RF2 content file
	 */
	@Override
	public RF2File createRF2File(Path parent, RF2Specification specification) {
		final String[] header = RF2ContentFile.extractHeader(parent.resolve(toString()));
		return specification.getRelease()
				.getContent()
				.fileSpecifications()
				.filter(spec -> isRecognized(this, header, spec))
				.findFirst()
				.<RF2File>map(spec -> new RF2ContentFile(parent, this, spec))
				.orElse(new RF2UnrecognizedFile(parent, this));
	}
	
	private boolean isRecognized(RF2ContentFileName fileName, String[] header, RF2ContentFileSpecification specToRecognize) {
		if (!Objects.equals(fileName.getElement(RF2ContentType.class).map(RF2ContentType::getContentType).orElse("N/A"), specToRecognize.getContentType())) {
			return false;
		}
		
		if (!Arrays.equals(header, specToRecognize.getHeader())) {
			return false;
		}
		
		return true;
	}
	
}
