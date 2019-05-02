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
package com.b2international.rf2.spec;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.naming.RF2ContentFileName;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2FileType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 0.3
 */
public final class RF2ContentFileSpecification {

	private final String[] header;
	private final String fileType;
	private final String contentType;
	private final String summary;
	private final String languageCode;

	@JsonCreator
	public RF2ContentFileSpecification(
			@JsonProperty("header") String[] header, 
			@JsonProperty("fileType") String fileType,
			@JsonProperty("contentType") String contentType, 
			@JsonProperty("summary") String summary, 
			@JsonProperty("languageCode") String languageCode) {
		this.header = header;
		if (Strings.isNullOrEmpty(fileType)) {
			if (RF2ContentType.isRefset(contentType)) {
				this.fileType = RF2FileType.DER2.toString();
			} else {
				this.fileType = RF2FileType.SCT2.toString();
			}
		} else {
			this.fileType = fileType;
		}
		this.contentType = contentType;
		this.summary = summary;
		this.languageCode = languageCode;
	}

	public String[] getHeader() {
		return header;
	}

	public String getFileType() {
		return fileType;
	}

	public String getContentType() {
		return contentType;
	}

	public String getSummary() {
		return summary;
	}

	public String getLanguageCode() {
		return languageCode;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(header), fileType, contentType, summary, languageCode);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2ContentFileSpecification other = (RF2ContentFileSpecification) obj;
		return Arrays.equals(header, other.header)
				&& Objects.equals(fileType, other.fileType)
				&& Objects.equals(contentType, other.contentType)
				&& Objects.equals(summary, other.summary)
				&& Objects.equals(languageCode, other.languageCode);
	}
	
	public RF2ContentFileSpecification merge(RF2ContentFileSpecification other) {
		return new RF2ContentFileSpecification(
			Optional.ofNullable(other.header).orElse(header), 
			Optional.ofNullable(other.fileType).orElse(fileType), 
			Optional.ofNullable(other.contentType).orElse(contentType), 
			Optional.ofNullable(other.summary).orElse(summary), 
			Optional.ofNullable(other.languageCode).orElse(languageCode)
		);
	}

	public RF2File prepare(Path parent, String contentSubType, String country, String namespace, String releaseDate) {
		final String name = String.join(RF2FileName.ELEMENT_SEPARATOR, fileType, contentType, new RF2ContentSubType(summary, contentSubType, languageCode).toString(), country + namespace, releaseDate);
		final String fileName = String.join(RF2FileName.FILE_EXT_SEPARATOR, name, RF2File.TXT);
		return new RF2ContentFile(parent, new RF2ContentFileName(fileName), this);
	}

}
