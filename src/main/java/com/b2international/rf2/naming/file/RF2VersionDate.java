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
@RF2NamePattern("([0-9]{8})")
public final class RF2VersionDate implements RF2NameElement {

	private final String versionDate;

	public RF2VersionDate(String versionDate) {
		this.versionDate = Objects.requireNonNull(versionDate);
	}
	
	public String getVersionDate() {
		return versionDate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(versionDate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2VersionDate other = (RF2VersionDate) obj;
		return Objects.equals(versionDate, other.versionDate);
	}
	
	@Override
	public String toString() {
		return versionDate;
	}
	
}
