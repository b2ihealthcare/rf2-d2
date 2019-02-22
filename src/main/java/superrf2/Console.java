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

	public static final PrintStream OUT = System.out;
	public static final PrintStream ERR = System.err;
	
	public final void error(String message, Object...args) {
		ERR.println(String.format("ERROR: " + message, args));
	}
	
	public final void log(String message, Object...args) {
		OUT.println(String.format(message, args));
	}
	
}
