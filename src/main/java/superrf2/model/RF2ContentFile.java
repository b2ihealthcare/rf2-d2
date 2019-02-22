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
package superrf2.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import superrf2.Console;
import superrf2.Constants;

/**
 * @since 0.1
 */
public abstract class RF2ContentFile extends RF2File {

	public RF2ContentFile(Path path) {
		super(path);
	}
	
	/**
	 * @return the RF2 header row for this kind of RF2 files.
	 */
	public abstract String[] getHeader();

	@Override
	public void printInfo(Console console) throws IOException {
		super.printInfo(console);
		console.log("Number of lines: %d", Files.lines(getPath(), Constants.UTF8).count());
	}
	
}
