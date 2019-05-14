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
package com.b2international.rf2.console;

import java.util.Objects;

/**
 * @since 0.3.2
 */
public abstract class DelegatingConsole implements Console {

	private final Console console;

	public DelegatingConsole(Console console) {
		this.console = Objects.requireNonNull(console);
	}
	
	@Override
	public final void warn(String message, Object... args) {
		console.warn(message, args);
	}

	@Override
	public final void error(String message, Object... args) {
		console.error(message, args);
	}

	@Override
	public final void log(String message, Object... args) {
		console.log(message, args);
	}

	@Override
	public final Console withIndentation(int indentation) {
		return console.withIndentation(indentation);
	}

	@Override
	public final Console withPrefix(String linePrefix) {
		return console.withPrefix(linePrefix);
	}
	
	protected final Console getConsole() {
		return console;
	}

}
