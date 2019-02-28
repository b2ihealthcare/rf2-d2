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
package superrf2.naming;

import java.util.regex.Pattern;

/**
 * @since 0.1
 */
public interface RF2NameElement {
	
	/**
	 * @param type
	 * @return the RF2 naming pattern of the given {@link RF2NameElement} subclass compiled to a regex {@link Pattern}. 
	 */
	static Pattern getNamingPattern(Class<?> type) {
		if (!RF2NameElement.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Only subtypes of " + RF2NameElement.class.getName() + " are accepted.");
		}
		if (!type.isAnnotationPresent(RF2NamePattern.class)) {
			throw new IllegalArgumentException("The type " + type.getName() + " must have an " + RF2NamePattern.class.getName() + " annotation present to get the naming pattern.");
		}
		return Pattern.compile(type.getAnnotation(RF2NamePattern.class).value());
	}
	
}
