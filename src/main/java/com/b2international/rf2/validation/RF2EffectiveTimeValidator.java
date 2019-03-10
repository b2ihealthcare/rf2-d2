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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.b2international.rf2.check.RF2IssueAcceptor;


/**
 * @since 0.1
 */
public final class RF2EffectiveTimeValidator {
	
	private RF2EffectiveTimeValidator() {}
	
	private final static SimpleDateFormat format = new SimpleDateFormat("YYYYMMDD");
	
	/**
	 * @param effectiveTime - effective time to check
	 * @param acceptor - to report issues
	 * @return <code>true</code> if the given effective time is a valid (YYYYMMDD) effective time, <code>false</code> otherwise.
	 */
	public static void validate(String effectiveTime, RF2IssueAcceptor acceptor) {
		try {
			if (!effectiveTime.isEmpty()) {
				format.parse(effectiveTime);
			}
		} catch (ParseException e) {
			acceptor.error("Effective time '%s' is not in the YYYYMMDD format.", effectiveTime);
		}
	}
	
}