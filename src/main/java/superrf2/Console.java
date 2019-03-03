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
package superrf2;

import java.io.PrintStream;

/**
 * @since 0.1
 */
public final class Console {

	private static final int DEFAULT_INDENTATION = 0;
	private static final int TAB_SIZE = 2;
	
	public static final PrintStream OUT = System.out;
	public static final PrintStream ERR = System.err;

	private final int indentation;

	public Console() {
		this(DEFAULT_INDENTATION);
	}
	
	public Console(int indentation) {
		this.indentation = indentation;
	}
	
	public final void error(String message, Object...args) {
		String tab = Constants.SPACE.repeat(indentation * TAB_SIZE);
		ERR.println(String.format("%sERROR: %s", tab, String.format(message, args)));
	}
	
	public final void log(String message, Object...args) {
		String tab = Constants.SPACE.repeat(indentation * TAB_SIZE);
		OUT.println(String.format("%s%s", tab, String.format(message, args)));
	}
	
	public Console indent(int indent) {
		return DEFAULT_INDENTATION == indent ? this : new Console(indent);
	}
	
}
