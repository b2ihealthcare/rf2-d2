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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.b2international.rf2.spec.RF2Specification;
import com.google.common.base.Strings;

import picocli.CommandLine.Command;

/**
 * @since 0.1
 */
@Command(
	usageHelpWidth = 120,
	description = "TODO",
	headerHeading = "Usage:%n%n",
    synopsisHeading = "",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n"
)
public abstract class RF2Command implements Runnable {

	private static final String VERSION_PROPERTY = "version";
	private static final String DEV_VERSION = "@version@";
	protected static final Path WORK_DIR = Paths.get(System.getProperty("user.dir"));
	
	private static RF2Specification SPECIFICATION;
	
	protected final Console console = new Console();

	@Override
	public final void run() {
		try {
			doRun();
		} catch (Exception e) {
			if (Strings.isNullOrEmpty(e.getMessage())) {
				console.error("Failed to run command. Unexpected error:");
				e.printStackTrace();
			} else {
				console.log(e.getMessage());
				if (isDevVersion()) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Actual command implementation.
	 * @throws Exception
	 */
	protected abstract void doRun() throws Exception;
	
	/**
	 * @return the current {@link RF2Specification} instance read from the working directory and merged with the default spec file.
	 * @throws IOException
	 */
	public static final RF2Specification getRF2Specification() throws IOException {
		if (SPECIFICATION == null) {
			SPECIFICATION = RF2Specification.get(WORK_DIR);
		}
		return SPECIFICATION;
	}
	
	/**
	 * @return the cli.properties file content as {@link Properties}
	 */
	public static final Properties getProperties() {
		try {
			var properties = new Properties();
			properties.load(RF2.class.getResourceAsStream("/cli.properties"));
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the CLI version from the {@link #getProperties() properties}.
	 */
	public static String getVersion() {
		return getProperties().getProperty(VERSION_PROPERTY);
	}
	
	/**
	 * @return <code>true</code> if the CLI is currently running in development mode, or <code>false</code> if in production mode.
	 */
	public static final boolean isDevVersion() {
		return DEV_VERSION.equals(getVersion());
	}

}
