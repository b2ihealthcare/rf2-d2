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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import superrf2.model.RF2File;

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
				System.err.println("Failed to read path: " + path);
				e.printStackTrace();
			}
		}
	}
	
	private void check(final Path path) throws IOException {
		if (Files.isDirectory(path)) {
			console.log("Directories are not supported yet! Path: %s", path);
		} else {
			RF2File rf2File = RF2File.detect(path);
			rf2File.printInfo(console);
		}
	}
	
}
