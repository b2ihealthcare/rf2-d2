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
@RF2NamePattern("(x|z)?(sct|der|doc|res|tls)(1|2)?")
public final class RF2FileType implements RF2NameElement {

	public static final RF2FileType SCT1 = new RF2FileType("", "sct", "1");
	public static final RF2FileType SCT2 = new RF2FileType("", "sct", "2");
	public static final RF2FileType DER2 = new RF2FileType("", "der", "2");
	
	private final String status;
	private final String type;
	private final String format;
	
	public RF2FileType(
		String status,
		String type,
		String format) {
		this.status = Objects.toString(status, "");
		this.type = Objects.requireNonNull(type);
		this.format = Objects.toString(format, "");
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getType() {
		return type;
	}
	
	public String getFormat() {
		return format;
	}
	
	@Override
	public String toString() {
		return String.join("", status, type, format);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(status, type, format);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2FileType other = (RF2FileType) obj;
		return Objects.equals(status, other.status)
				&& Objects.equals(type, other.type)
				&& Objects.equals(format, other.format);
	}
	
}
