/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sggetComponentCategory
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
package com.b2international.rf2.validation;

import com.b2international.rf2.check.RF2IssueAcceptor;

/**
 * @since 0.1
 */
public final class RF2StatusValidator {
	
	private RF2StatusValidator() {}
	
	/**
	 * @param componentStatus - the component status
	 * @param acceptor - to report issues
	 * @return <code>true</code> if the given component status is a valid (0 or 1), <code>false</code> otherwise.
	 */
	public static void validate(String componentStatus, RF2IssueAcceptor acceptor) {
		if (componentStatus.isEmpty()) {
			acceptor.error("Status cannot be empty");
		}
		
		if (!componentStatus.equals("0") && !componentStatus.equals("1")) {
			acceptor.error("'%s' is not a valid component status.", componentStatus);
		}
	}
	
}