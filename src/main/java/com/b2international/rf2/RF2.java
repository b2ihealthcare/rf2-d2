/*
 * Copyright 2019-2020 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

/**
 * @since 0.1
 */
@Command(
	name = "rf2",
	description = "RF2-D2, a SNOMED CT release tool.",
	subcommands = {
		RF2Check.class,
		RF2Create.class,
		RF2Transform.class,
		RF2Diff.class,
		HelpCommand.class
	},
	versionProvider = RF2.VersionProvider.class
)
public final class RF2 extends RF2Command {

	@Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version information and exit.")
	boolean versionInfoRequested;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
	boolean usageHelpRequested;

	@Override
	public void doRun(RF2Specification specification) throws Exception {
		CommandLine.usage(this, System.out, CommandLine.Help.Ansi.AUTO);
	}
	
	public static void main(String[] args) {
		CommandLine.run(new RF2(), args);
	}
	
	/**
	 * @since 0.1
	 */
	public static final class VersionProvider implements IVersionProvider {

		@Override
		public String[] getVersion() throws Exception {
			return new String[] {
				"RF2-D2 v" + RF2Command.getVersion() + " @Copyright 2019 B2i Healthcare",
				"Supported RF2 Version: " + getRF2Specification().getRf2Version()
			};
		}
		
	}
	
}
