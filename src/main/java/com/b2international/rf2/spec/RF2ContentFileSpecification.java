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
package com.b2international.rf2.spec;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * @since 0.3
 */
public final class RF2ContentFileSpecification {

	private final String[] header;
	private final RF2FileType fileType;
	private final String contentType;
	private final List<RF2Filter> inclusions;
	private final List<RF2Filter> exclusions;
	private final String contentSubType;
	private final String summary;
	private final String languageCode;
	private final String extension;

	@JsonCreator
	public RF2ContentFileSpecification(
			@JsonProperty("header") String[] header, 
			@JsonProperty("fileType") String fileType,
			@JsonProperty("contentType") String contentType,
			@JsonProperty("include") List<RF2Filter> inclusions,
			@JsonProperty("exclude") List<RF2Filter> exclusions,
			@JsonProperty("contentSubType") String contentSubType,
			@JsonProperty("summary") String summary, 
			@JsonProperty("languageCode") String languageCode,
			@JsonProperty("extension") String extension) {
		this(
			header, 
			fileType == null 
				? null 
				: Strings.isNullOrEmpty(fileType) ? RF2FileType.EMPTY : RF2FileType.valueOf(fileType), 
			contentType,
			inclusions,
			exclusions,
			contentSubType, 
			summary, 
			languageCode, 
			extension
		);
	}
	
	public RF2ContentFileSpecification(
			String[] header,
			RF2FileType fileType,
			String contentType,
			List<RF2Filter> inclusions,
			List<RF2Filter> exclusions,
			String contentSubType,
			String summary,
			String languageCode,
			String extension) {
		this.header = header;
		if (fileType == null) {
			if (RF2ContentType.isRefset(contentType)) {
				this.fileType = RF2FileType.DER2;
			} else {
				this.fileType = RF2FileType.SCT2;
			}
		} else {
			this.fileType = fileType;
		}
		this.contentType = contentType;
		this.inclusions = inclusions;
		this.exclusions = exclusions;
		this.contentSubType = contentSubType;
		this.summary = summary;
		this.languageCode = languageCode;
		this.extension = Strings.isNullOrEmpty(extension) ? RF2File.TXT : extension;
	}
	
	@JsonIgnore
	public boolean isDataFile() {
		return fileType.isData();
	}
	
	public RF2ContentFileSpecification merge(RF2ContentFileSpecification other) {
		return new RF2ContentFileSpecification(
			Optional.ofNullable(other.header).orElse(header), 
			Optional.ofNullable(other.fileType).orElse(fileType), 
			Optional.ofNullable(other.contentType).orElse(contentType),
			Optional.ofNullable(other.inclusions).orElse(inclusions),
			Optional.ofNullable(other.exclusions).orElse(exclusions),
			Optional.ofNullable(other.contentSubType).orElse(contentSubType), 
			Optional.ofNullable(other.summary).orElse(summary), 
			Optional.ofNullable(other.languageCode).orElse(languageCode),
			Optional.ofNullable(other.extension).orElse(extension)
		);
	}

	public RF2File prepare(Path parent, RF2ReleaseSpecification specification, String contentSubType) {
		final String contentSubTypeToUse = Strings.isNullOrEmpty(this.contentSubType) ? contentSubType : this.contentSubType;
		final RF2ContentSubType rf2ContentSubType = new RF2ContentSubType(summary, contentSubTypeToUse, languageCode);

		final String name = Joiner.on(RF2FileName.ELEMENT_SEPARATOR)
			.skipNulls()
			.join(fileType.isEmpty() ? null : fileType, contentType, rf2ContentSubType.isEmpty() ? null : rf2ContentSubType, specification.getCountry() + specification.getNamespace(), specification.getDate());
		final String fileName = String.join(RF2FileName.FILE_EXT_SEPARATOR, name, extension);
		return new RF2ContentFile(parent, new RF2ContentFileName(fileName), this);
	}


	public String[] getHeader() {
		return header;
	}

	public RF2FileType getFileType() {
		return fileType;
	}

	public String getContentType() {
		return contentType;
	}

	public List<RF2Filter> getInclusions() {
		return inclusions;
	}

	public List<RF2Filter> getExclusions() {
		return exclusions;
	}

	public String getContentSubType() {
		return contentSubType;
	}

	public String getSummary() {
		return summary;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getExtension() {
		return extension;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(header), fileType, contentType, contentSubType, summary, languageCode, extension);
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
				&& Objects.equals(inclusions, other.inclusions)
				&& Objects.equals(exclusions, other.exclusions)
				&& Objects.equals(contentSubType, other.contentSubType)
				&& Objects.equals(summary, other.summary)
				&& Objects.equals(languageCode, other.languageCode)
				&& Objects.equals(extension, other.extension);
	}

}
