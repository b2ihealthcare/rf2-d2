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
package com.b2international.rf2.naming.file;

import java.util.Objects;

import com.b2international.rf2.naming.RF2NameElement;
import com.b2international.rf2.naming.RF2NamePattern;

/**
 * @since 0.1
 */
@RF2NamePattern("(.*)(Full|Snapshot|Delta|Current|Draft|Review)(?:-([a-z]{2}(?:-[A-Za-z]{2})?)?)?")
public final class RF2ContentSubType implements RF2NameElement {

	private final String summary;
	private final String releaseType;
	private final String languageCode;

	public RF2ContentSubType(String summary, String releaseType, String languageCode) {
		this.summary = Objects.toString(summary, "");
		this.releaseType = Objects.requireNonNull(releaseType);
		this.languageCode = Objects.toString(languageCode, "");
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getReleaseType() {
		return releaseType;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(summary, releaseType, languageCode);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2ContentSubType other = (RF2ContentSubType) obj;
		return Objects.equals(summary, other.summary)
				&& Objects.equals(releaseType, other.releaseType)
				&& Objects.equals(languageCode, other.languageCode);
	}
	
	@Override
	public String toString() {
		return String.join("", summary, releaseType, languageCode.isEmpty() ? "" : "-".concat(languageCode));
	}
	
}
