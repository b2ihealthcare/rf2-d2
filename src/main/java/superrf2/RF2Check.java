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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import superrf2.model.RF2ContentFile;
import superrf2.model.RF2File;

/**
 * @since 0.1
 */
@Command(
	name = "check",
	description = "Checks a set of RF2 files and/or archives against the current RF2 specification"
)
public class RF2Check extends RF2Command {

	private static final String PATH_DESCRIPTION = "RF2 source files to check. Accepted file types: *.txt,*.zip.";
	
	@Parameters(arity = "1..*", description = RF2Check.PATH_DESCRIPTION, paramLabel = "PATH")
	List<String> paths;
	
	@Override
	public void run() {
		for (String path : paths) {
			try {
				check(Paths.get(path));
			} catch (Exception e) {
				console.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void check(final Path path) throws IOException {
		RF2File.detect(path).visit(file -> {
			try {
				checkRF2File(file, file.getPath().equals(path));
			} catch (IOException e) {
				console.error("Couldn't check RF2File at %s", file.getFileName());
			}
		});
	}
	
	private void checkRF2File(RF2File file, boolean isPathArgument) throws IOException {
		int indentation = isPathArgument ? 0 : file.getPath().getNameCount();
		final Console console = this.console.indent(indentation);
		console.log("%s", file.getFileName());
		
		final Console detailConsole = this.console.indent(indentation + 1);
		detailConsole.log("Type: %s", file.getType());
		if (file instanceof RF2ContentFile) {
			RF2ContentFile rf2ContentFile = (RF2ContentFile) file;
			detailConsole.log("Header: %s", Arrays.toString(rf2ContentFile.getHeader()));
			detailConsole.log("Number of lines: %d", rf2ContentFile.rows().count());
		}
	}
	
}
