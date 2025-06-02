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
package com.b2international.rf2.console;

import java.io.PrintStream;

import com.b2international.rf2.Constants;
import com.google.common.base.Preconditions;

/**
 * @since 0.1
 */
final class SystemConsole implements Console {

	private static final int DEFAULT_INDENTATION = 0;
	private static final int TAB_SIZE = 2;
	
	public static final PrintStream OUT = System.out;
	public static final PrintStream ERR = System.out;

	private final int indentation;
	private final String linePrefix;

	public SystemConsole() {
		this(DEFAULT_INDENTATION, "");
	}
	
	public SystemConsole(int indentation, String linePrefix) {
		this.indentation = indentation;
		this.linePrefix = linePrefix;
	}
	
	@Override
	public final void warn(String message, Object...args) {
		String tab = Constants.SPACE.repeat(indentation * TAB_SIZE);
		ERR.println(String.format("%s%sWARN: %s", tab, linePrefix, String.format(message, args)));
	}
	
	@Override
	public final void error(String message, Object...args) {
		String tab = Constants.SPACE.repeat(indentation * TAB_SIZE);
		ERR.println(String.format("%s%sERROR: %s", tab, linePrefix, String.format(message, args)));
	}
	
	@Override
	public final void log(String message, Object...args) {
		String tab = Constants.SPACE.repeat(indentation * TAB_SIZE);
		OUT.println(String.format("%s%s%s", tab, linePrefix, String.format(message, args)));
	}
	
	@Override
	public Console withIndentation(int indentation) {
		return new SystemConsole(indentation, linePrefix);
	}
	
	@Override
	public Console indent(int indentWith) {
		Preconditions.checkArgument(indentWith >= 0, "indentWith parameter should be greater than or equal to 0. Got: %s", indentWith);
		if (indentWith == 0) {
			return this;
		} else {
			return new SystemConsole(this.indentation + indentWith, linePrefix);
		}
	}
	
	@Override
	public Console withPrefix(String linePrefix) {
		return new SystemConsole(indentation, linePrefix);
	}
	
}
