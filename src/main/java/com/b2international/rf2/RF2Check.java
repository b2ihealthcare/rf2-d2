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
package com.b2international.rf2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.console.Console;
import com.b2international.rf2.model.RF2ContentFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @since 0.1
 */
@Command(
	name = "check",
	description = "Checks a set of RF2 files and/or archives against the current RF2 specification"
)
public final class RF2Check extends RF2Command {

	private static final String PATH_DESCRIPTION = "RF2 source files to check.";
	
	@Parameters(arity = "1..*", description = PATH_DESCRIPTION, paramLabel = "PATH", converter = RF2FileTypeConverter.class)
	List<RF2File> sources;
	
	@Override
	public void doRun(RF2Specification specification) throws Exception {
		for (RF2File source : sources) {
			check(source);
		}
	}
	
	private void check(final RF2File file) throws IOException {
		file.visit(content -> {
			try {
				checkRF2File(content, content.getPath().equals(file.getPath()));
			} catch (IOException e) {
				console.error("Couldn't check RF2File at %s", content.getRF2FileName());
			}
		});
	}
	
	private void checkRF2File(RF2File file, boolean isPathArgument) throws IOException {
		int indentation = isPathArgument ? 0 : file.getPath().getNameCount();
		final Console console = this.console.withIndentation(indentation);
		console.log(file.getRF2FileName().toString());
		
		final Console detailConsole = this.console.withIndentation(indentation + 1).withPrefix("-");
		detailConsole.log("type: %s", file.getType());
		if (file instanceof RF2ContentFile) {
			RF2ContentFile rf2ContentFile = (RF2ContentFile) file;
			if (rf2ContentFile.isDataFile()) {
				detailConsole.log("header: %s", Arrays.toString(rf2ContentFile.getHeader()));
				detailConsole.log("lines: %d", rf2ContentFile.rowsParallel().count());
			}
		}

		// check all RF2 files
		final RF2IssueAcceptor issueAcceptor = new RF2IssueAcceptor();
		file.check(issueAcceptor);
		
		final Console issueConsole = detailConsole.withIndentation(indentation + 2);
		
		if (issueAcceptor.hasIssues()) {
			detailConsole.log("issues:");
			issueAcceptor.getErrors().forEach(issueConsole::error);
			issueAcceptor.getWarns().forEach(issueConsole::warn);
		}
		
	}
	
}
