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
package com.b2international.rf2.naming.release;

import java.util.Objects;

import com.b2international.rf2.naming.RF2NameElement;
import com.b2international.rf2.naming.RF2NamePattern;

/**
 * @since 0.1
 */
@RF2NamePattern("(.+?)(Edition|Extension)?(RF1|RF2)?")
public final class RF2Product implements RF2NameElement {

	private final String product;
	private final String scope;
	private final String format;
	
	public RF2Product(String product, String scope, String format) {
		this.product = Objects.requireNonNull(product);
		this.scope = Objects.toString(scope, "");
		this.format = Objects.toString(format, "");
	}
	
	public String getProduct() {
		return product;
	}
	
	public String getScope() {
		return scope;
	}
	
	public String getFormat() {
		return format;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(product, scope, format);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RF2Product other = (RF2Product) obj;
		return Objects.equals(product, other.product)
				&& Objects.equals(scope, other.scope)
				&& Objects.equals(format, other.format);
	}

	@Override
	public String toString() {
		return String.join("", product, scope, format);
	}
	
}
