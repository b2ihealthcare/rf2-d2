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
package com.b2international.rf2.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 0.1
 */
public final class RF2IssueAcceptor {

	private List<String> errors = Collections.synchronizedList(new ArrayList<>());
	private List<String> warns = Collections.synchronizedList(new ArrayList<>());
	
	public void warn(String message, Object...args) {
		warns.add(String.format(message, args));
	}
	
	public void error(String message, Object...args) {
		errors.add(String.format(message, args));
	}
	
	public List<String> getErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	public List<String> getWarns() {
		return Collections.unmodifiableList(warns);
	}

	public boolean hasIssues() {
		return !getErrors().isEmpty() || !getWarns().isEmpty();
	}
	
}
