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
package com.b2international.rf2.naming.release;

import java.util.Objects;

import com.b2international.rf2.naming.RF2NameElement;
import com.b2international.rf2.naming.RF2NamePattern;

/**
 * @since 0.1
 */
@RF2NamePattern("(ALPHA|BETA|PRODUCTION)")
public final class RF2ReleaseStatus implements RF2NameElement {

	private final String releaseStatus;

	public RF2ReleaseStatus(String releaseStatus) {
		this.releaseStatus = Objects.requireNonNull(releaseStatus);
	}
	
	public String getReleaseStatus() {
		return releaseStatus;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(releaseStatus);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2ReleaseStatus other = (RF2ReleaseStatus) obj;
		return Objects.equals(releaseStatus, other.releaseStatus);
	}
	
	@Override
	public String toString() {
		return releaseStatus;
	}
	
}
