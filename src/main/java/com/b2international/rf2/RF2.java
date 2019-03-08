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
package com.b2international.rf2;

import java.util.Properties;

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
	subcommands = {
		RF2Check.class,
		RF2Create.class,
		HelpCommand.class
	},
	versionProvider = RF2.VersionProvider.class
)
public class RF2 implements Runnable {

	public static final String RF2_VERSION = "20190131";
	
	@Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version information and exit.")
	boolean versionInfoRequested;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
	boolean usageHelpRequested;

	@Override
	public void run() {
		CommandLine.usage(this, System.out, CommandLine.Help.Ansi.AUTO);
	}
	
	public static void main(String[] args) {
		CommandLine.run(new RF2(), args);
	}
	
	public static final class VersionProvider implements IVersionProvider {
		
		@Override
		public String[] getVersion() throws Exception {
			var properties = new Properties();
			properties.load(getClass().getResourceAsStream("/cli.properties"));
			return new String[] {
				"RF2-D2 v" + properties.getProperty("version") + " @Copyright 2019 B2i Healthcare",
				"Supported RF2 Version: " + RF2_VERSION
			};
		}
		
	}
	
}
