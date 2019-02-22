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
package superrf2.model;

/**
 * @since 0.1
 */
public enum RF2FileType {

	/**
	 * RF2 Archive/Bundle File (aka ZIP)
	 */
	BUNDLE,
	
	/**
	 * Terminology Data File
	 */
	SCT,

	/**
	 * Derivative Work Data File
	 */
	DER,

	/**
	 * Implementation Resource Data File
	 */
	RES,

	/**
	 * Implementation Resource Tool
	 */
	TLS,

	/**
	 * Documentation
	 */
	DOC;

	public String getWithArchivalPrefix() {
		return String.format("z%s", toString());
	}

	public String getWithTestPrefix() {
		return String.format("x%s", toString());
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
	public static final RF2FileType of(String name) {
		return valueOf(name.toUpperCase());
	}
	
}