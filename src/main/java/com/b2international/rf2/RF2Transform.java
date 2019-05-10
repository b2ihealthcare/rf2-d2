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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 0.3
 */
@Command(
	name = "transform",
	description = "Apply an expression to transform and/or filter RF2 rows in the given RF2 source."
)
public class RF2Transform extends RF2Command {

	private static final String GROOVY_EXT = ".groovy";
	
	private static final String PATH_DESCRIPTION = "RF2 source file to replace column values in.";
	private static final String SCRIPT_DESCRIPTION = "Script Expression or PATH to a .groovy script file to apply to each RF2 line in the specified source file.";
	private static final String OUTDIR_DESCRIPTION = "Output directory where the transformed RF2 output file will be created.";
	
	@Parameters(arity = "1", paramLabel = "PATH", description = PATH_DESCRIPTION, index = "0")
	String path;
	
	@Parameters(arity = "1", paramLabel = "SCRIPT", description = SCRIPT_DESCRIPTION, index = "1")
	String script;
	
	@Option(required = false, names = {"-o", "--outdir"}, description = OUTDIR_DESCRIPTION)
	String outDir = "target";
	
	@Override
	protected void doRun() throws Exception {
		final String rawScript;
		if (script.endsWith(GROOVY_EXT)) {
			Path scriptPath = Paths.get(script);
			if (!Files.exists(scriptPath)) {
				console.log("The specified script file at '%s' does not exist", scriptPath);
				return;
			}
			rawScript = Files.readString(scriptPath);
		} else {
			rawScript = script;
		}

		if (!Files.exists(Paths.get(path))) {
			console.log("The specified source at '%s' does not exist", path);
			return;
		}

		final Path outputDirectory = Paths.get(outDir);
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory);
		}

		final RF2Specification defaultSpecification = getRF2Specification();
		final RF2File sourceFile = defaultSpecification.detect(Paths.get(path));
		sourceFile.transform(new RF2TransformContext(rawScript, defaultSpecification, outputDirectory, console));
	}

}
