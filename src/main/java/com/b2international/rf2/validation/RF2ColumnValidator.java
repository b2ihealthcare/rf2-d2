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
package com.b2international.rf2.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.model.RF2Columns;
import com.b2international.rf2.model.RF2ContentFile;

/**
 * @since 0.1
 */
public interface RF2ColumnValidator {

	/**
	 * No-op RF2 column validator.
	 */
	static RF2ColumnValidator NOOP = new RF2ColumnValidator() {
		@Override
		public Set<String> getColumns() {
			return Set.of();
		}
		
		@Override
		public void check(RF2ContentFile file, String columnHeader, String columnValue, RF2IssueAcceptor acceptor) {
		}
	};
	
	/**
	 * Allows any value in a given column.
	 */
	static RF2ColumnValidator ALLOW_EMPTY = new RF2ColumnValidator() {
		@Override
		public Set<String> getColumns() {
			return Set.of(
				RF2Columns.MAP_TARGET, 
				RF2Columns.PARENT_DOMAIN,
				RF2Columns.PROXIMAL_PRIMITIVE_REFINEMENT
			);
		}
		
		@Override
		public void check(RF2ContentFile file, String columnHeader, String columnValue, RF2IssueAcceptor acceptor) {
		}
	};
	
	/**
	 * All available {@link RF2ColumnValidator}s.
	 */
	static Map<String, RF2ColumnValidator> VALIDATORS = 
			List.of(
				// register new validators here
				new RF2IdentifierValidator(),
				new RF2EffectiveTimeValidator(),
				new RF2BooleanValidator(),
				new RF2ComponentIDValidator(),
				new RF2IntegerValidator(),
				new RF2LanguageCodeValidator(),
				new RF2NotEmptyValidator(),
				ALLOW_EMPTY
			)
			.stream()
			.flatMap(validator -> {
				return validator.getColumns().stream().map(column -> Map.entry(column, validator));
			})
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	
	/**
	 * @return a {@link Set} of column names that can be validated with this validator.
	 */
	Set<String> getColumns();
	
	/**
	 * Validates the columnValue argument in the given file and reports problems using the given {@link RF2IssueAcceptor}.
	 * 
	 * @param file - the file that holds the columnValue
	 * @param columnHeader - the current header where the value is stored
	 * @param columnValue - the value to validate
	 * @param acceptor - the issue acceptor to report errors/warnings to
	 */
	void check(RF2ContentFile file, String columnHeader, String columnValue, RF2IssueAcceptor acceptor);
	
}
