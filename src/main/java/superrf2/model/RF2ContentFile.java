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
import java.util.function.Consumer;
import java.util.stream.Stream;

import superrf2.Constants;
import superrf2.naming.RF2FileName;
import superrf2.naming.file.RF2ContentType;

/**
 * @since 0.1
 */
public abstract class RF2ContentFile extends RF2File {

	private String[] header;
	
	public RF2ContentFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	@Override
	public String getType() {
		return getFileName()
				.getElement(RF2ContentType.class)
				.map(RF2ContentType::getContentType)
				// TODO recognize type from header
				.orElse("Unknown");
	}
	
	@Override
	public void visit(Consumer<RF2File> visitor) {
		visitor.accept(this);
	}
	
	/**
	 * @return the current RF2 header by reading the first line of the file or if this is a non-existing file returns the header from the spec for kind of RF2 files
	 * @throws IOException 
	 */
	public final String[] getHeader() throws IOException {
		if (header == null) {
			if (Files.exists(getPath())) {
				header = Files.lines(getPath()).findFirst().orElse("N/A").split(Constants.TAB);
			} else {
				final RF2Header rf2Header = getClass().getAnnotation(RF2Header.class);
				if (rf2Header == null) {
					throw new IllegalStateException("Missing RF2 Header specification on type: " + getClass().getName());
				}
				header = rf2Header.value();
			}
		}
		return header;
	}
	
	/**
	 * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects
	 * @throws IOException
	 */
	public final Stream<String[]> rows() throws IOException {
		return Files.lines(getPath())
				.skip(1)
				.map(line -> line.split(Constants.TAB));
	}

}
