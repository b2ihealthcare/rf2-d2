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
package superrf2.naming.file;

import java.util.Objects;

import superrf2.naming.RF2NameElement;
import superrf2.naming.RF2NamePattern;

/**
 * @since 0.1
 */
@RF2NamePattern("(INT|[A-Z]{2})?([0-9]{7})?")
public final class RF2CountryNamespace implements RF2NameElement {

	public static final RF2CountryNamespace INT = new RF2CountryNamespace("INT", "");
	
	private final String countryCode;
	private final String namespaceId;

	public RF2CountryNamespace(String countryCode, String namespaceId) {
		this.countryCode = countryCode;
		this.namespaceId = namespaceId;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public String getNamespaceId() {
		return namespaceId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(countryCode, namespaceId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2CountryNamespace other = (RF2CountryNamespace) obj;
		return Objects.equals(countryCode, other.countryCode)
				&& Objects.equals(namespaceId, other.namespaceId);
	}
	
	@Override
	public String toString() {
		return String.join("", countryCode, namespaceId);
	}
	
}
