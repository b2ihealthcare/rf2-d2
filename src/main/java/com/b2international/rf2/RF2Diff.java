/*
 * Copyright 2020-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.rf2;

import com.b2international.rf2.console.Console;
import com.b2international.rf2.console.DelegatingConsole;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 0.4.0
 */
@Command(
	name = "diff",
	description = "Diff two RF2 archives or files and display the differences in a close to similar format as usual diff softwares (+/- rows)."
)
public final class RF2Diff extends RF2Command {

	private static final String BASE_DESCRIPTION = "RF2 archive or file to compare the compare RF2 archive or file argument against.";
	private static final String COMPARE_DESCRIPTION = "RF2 archive or file to compare against the base RF2 archive or file argument.";
	
	@Parameters(arity = "1", paramLabel = "BASE", description = BASE_DESCRIPTION, index = "0", converter = RF2FileTypeConverter.class)
	RF2File base;
	
	@Parameters(arity = "1", paramLabel = "COMPARE", description = COMPARE_DESCRIPTION, index = "1", converter = RF2FileTypeConverter.class)
	RF2File compare;

	@Option(names = {"-m", "--missing"}, description = "to extract only the component IDs that need to be removed form the BASE RF2 file to get the COMPARE RF2 file")
	boolean missingOnly = false;
	
	@Option(names = {"-n", "--new"}, description = "to extract only the component IDs that need to be added to the BASE RF2 file to get the COMPARE RF2 file")
	boolean newOnly = false;
	
	@Override
	protected void doRun(RF2Specification specification) throws Exception {
		final Console diffConsole;
		if (missingOnly) {
			if (newOnly) {
				console.error("Either use missing or new but not both");
				return;
			}
			diffConsole = new ChangeOnlyConsole(console, "-");
		} else if (newOnly) {
			diffConsole = new ChangeOnlyConsole(console, "+");
		} else {
			diffConsole = console;
		}
		compare.diff(base, diffConsole);
	}
	
	private static final class ChangeOnlyConsole extends DelegatingConsole {

		private final String changeKind;

		public ChangeOnlyConsole(Console console, String changeKind) {
			super(console);
			this.changeKind = changeKind;
		}
		
		@Override
		public void log(String message, Object... args) {
			// report only lines that start with `-`
			if (message.startsWith(changeKind)) {
				// strip of the changeKind start character to get the RF2 line
				super.log(message.substring(1), args);
			}
		}
		
		@Override
		public Console indent(int indentWith) {
			// ignore any indent request
			return this;
		}
		
	}

}
