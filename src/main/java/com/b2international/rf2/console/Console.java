/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;

import com.google.common.base.Stopwatch;

/**
 * @since 0.3.2
 */
public interface Console {

	void warn(String message, Object...args);

	void error(String message, Object...args);

	void log(String message, Object...args);

	Console withIndentation(int indentation);
	
	Console indent(int indentWith);

	Console withPrefix(String linePrefix);
	
	default ConsoleTask task(String taskDescription, Object...args) {
		return new ConsoleTask(this, taskDescription, args); 
	}
	
	static Console system() {
		return new SystemConsole();
	}
	
	final class ConsoleTask {
		
		private final Console console;
		private final String taskDescription;

		public ConsoleTask(Console console, String taskDescription, Object...args) {
			this.console = console;
			this.taskDescription = String.format(taskDescription, args);
		}
		
		public void run(Console.Task task) throws IOException {
			final Stopwatch w = Stopwatch.createStarted();
			try {
				console.log("Started %s", taskDescription);
				task.run();
			} finally {
				console.log("Finished %s [%s]", taskDescription, w);
			}
		}
		
	}
	
	@FunctionalInterface
	interface Task {
		
		void run() throws IOException;
		
	} 
	
}
