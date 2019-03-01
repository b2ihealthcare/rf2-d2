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
import java.util.Arrays;
import java.util.stream.Stream;

import superrf2.Console;
import superrf2.Constants;
import superrf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public abstract class RF2ContentFile extends RF2File {

	public RF2ContentFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	/**
	 * @return the current RF2 header by reading the first line of the file or if this is a non-existing file returns the header from the spec for kind of RF2 files
	 * @throws IOException 
	 */
	public final String[] getHeader() throws IOException {
		if (Files.exists(getPath())) {
			return Files.lines(getPath()).findFirst().orElse("N/A").split(Constants.TAB);
		} else {
			final RF2Header rf2Header = getClass().getAnnotation(RF2Header.class);
			if (rf2Header == null) {
				throw new IllegalStateException("Missing RF2 Header specification on type: " + getClass().getName());
			}
			return rf2Header.value();
		}
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public final Stream<String[]> rows() throws IOException {
		return Files.lines(getPath())
				.skip(1)
				.map(line -> line.split(Constants.TAB));
	}

	@Override
	public void printInfo(Console console) throws IOException {
		super.printInfo(console);
		console.log("Header: ", Arrays.toString(getHeader()));
		console.log("Number of lines: %d", rows().count());
	}
	
}
