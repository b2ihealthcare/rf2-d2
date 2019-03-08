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
@RF2NamePattern("(.+)")
public final class RF2ContentType implements RF2NameElement {

	public static final RF2ContentType CONCEPT = new RF2ContentType("Concept");
	public static final RF2ContentType DESCRIPTION = new RF2ContentType("Description");
	public static final RF2ContentType RELATIONSHIP = new RF2ContentType("Relationship");
	public static final RF2ContentType STATED_RELATIONSHIP = new RF2ContentType("StatedRelationship");
	public static final RF2ContentType TEXT_DEFINITION = new RF2ContentType("TextDefinition");
	public static final RF2ContentType IDENTIFIER = new RF2ContentType("Identifier");
	
	private final String contentType;

	public RF2ContentType(String contentType) {
		this.contentType = Objects.requireNonNull(contentType);
	}

	public String getContentType() {
		return contentType;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(contentType);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2ContentType other = (RF2ContentType) obj;
		return Objects.equals(contentType, other.contentType);
	}
	
	@Override
	public String toString() {
		return contentType;
	}
	
}
