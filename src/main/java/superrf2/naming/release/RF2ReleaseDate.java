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
package superrf2.naming.release;

import java.util.Objects;

import superrf2.naming.RF2NameElement;
import superrf2.naming.RF2NamePattern;

/**
 * @since 0.1
 */
@RF2NamePattern("^([0-9]{8})T([0-9]{6})Z$")
public final class RF2ReleaseDate implements RF2NameElement {

	private final String releaseDate;
	private final String releaseTime;
	
	public RF2ReleaseDate(String releaseDate, String releaseTime) {
		this.releaseDate = Objects.requireNonNull(releaseDate);
		this.releaseTime = Objects.requireNonNull(releaseTime);
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}
	
	public String getReleaseTime() {
		return releaseTime;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(releaseDate, releaseTime);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2ReleaseDate other = (RF2ReleaseDate) obj;
		return Objects.equals(releaseDate, other.releaseDate)
				&& Objects.equals(releaseTime, other.releaseTime);
	}
	
	@Override
	public String toString() {
		return String.format("%sT%sZ", releaseDate, releaseTime);
	}
	
}
