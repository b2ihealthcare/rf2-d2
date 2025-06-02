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
package com.b2international.rf2.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.console.Console;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.spec.RF2Specification;
import com.google.common.base.Preconditions;

/**
 * @since 0.1
 */
public final class RF2Directory extends RF2File {

	public static final String ROOT_PATH = "/";
	private final RF2Specification specification;

	public RF2Directory(Path parent, RF2FileName fileName, RF2Specification specification) {
		super(parent, fileName);
		this.specification = specification;
	}

	@Override
	public void visit(Consumer<RF2File> visitor) throws IOException {
		visitor.accept(this);
		Files.walk(getPath(), 1).forEach(path -> {
			if (!path.equals(getPath())) {
				try {
					specification.detect(path).visit(visitor);
				} catch (IOException e) {
					throw new RuntimeException("Couldn't visit path: " + path, e);
				}
			}
		});
	}
	
	@Override
	public String getType() {
		return "Directory";
	}
	
	@Override
	public void create(RF2CreateContext context) throws IOException {
		Files.createDirectories(getPath());
	}

	@Override
	public void transform(RF2TransformContext context) throws IOException {
		// there's nothing to transform so we'll just create the directory to its new location
		final RF2File newRF2Directory = getRF2FileName().createRF2File(context.getParent(), context.getSpecification());
		
		context.task("Creating directory '%s'", getPath()).run(() -> {
			Files.createDirectories(newRF2Directory.getPath());
		});
		
		Files.walk(getPath(), 1).forEach(path -> {
			if (!path.equals(getPath())) {
				try {
					specification.detect(path).transform(context.newSubContext(newRF2Directory.getPath()));
				} catch (IOException e) {
					throw new RuntimeException("Couldn't transform path: " + path, e);
				}
			}
		});
	}
	
	@Override
	public void diff(RF2File other, Console console) throws IOException {
		Preconditions.checkArgument(other instanceof RF2Directory, "RF2 Directory '%s' cannot be compared with file: '%s'", getRF2FileName(), other.getRF2FileName());
		RF2Directory otherDirectory = (RF2Directory) other;
		// walk the first level of the directory and search for similarly named files/directories
		Iterator<Path> compareFiles = listFiles().iterator();
		Iterator<Path> baseFiles = otherDirectory.listFiles().iterator();
		
		console.log("%s -> %s", getRF2FileName(), other.getRF2FileName());

		Path compareFile = compareFiles.hasNext() ? compareFiles.next() : null;
    	Path baseFile = baseFiles.hasNext() ? baseFiles.next() : null;
    	
    	while (compareFile != null && baseFile != null) {
    		RF2File compareRf2File = specification.detect(compareFile);
    		RF2File baseRf2File = specification.detect(baseFile);
    		
    		if (compareRf2File.isUnrecognized()) {
    			console.log("Unrecognized RF2 file: '%s'", compareRf2File.getPath());
    			// proceed to next recognizable compare file
    			compareFile = compareFiles.hasNext() ? compareFiles.next() : null;
    		} else if (baseRf2File.isUnrecognized()) {
    			console.log("Unrecognized RF2 file: '%s'", baseRf2File.getPath());
    			// proceed to next recognizable base file
    			baseFile = baseFiles.hasNext() ? baseFiles.next() : null;
    		} else if (compareRf2File.getType().equals(baseRf2File.getType())) {
    			// if types match, then do the diff
    			compareRf2File.diff(baseRf2File, console.indent(2));
        		compareFile = compareFiles.hasNext() ? compareFiles.next() : null;
        		baseFile = baseFiles.hasNext() ? baseFiles.next() : null;
    		} else {
    			// if types do NOT match, then proceed to the next available base file to see if there is a base file that can be diffed with the current compare file
    			// report the current base file as missing ("-") from compare
    			console.log("Unrecognized file: '%s'", baseRf2File.getPath());
        		baseFile = baseFiles.hasNext() ? baseFiles.next() : null;
    		}
    	}
    	
    	// if there are items in either of the streams, then register them as +/-
    	if (baseFile != null) {
    		console.log("-%s", baseFile.getFileName());
    		while (baseFiles.hasNext()) {
    			baseFile = baseFiles.next();
    			console.log("-%s", baseFile.getFileName());
    		}
    	}

    	if (compareFile != null) {
    		console.log("+%s", compareFile.getFileName());
    		while (compareFiles.hasNext()) {
    			compareFile = compareFiles.next();
    			console.log("+%s", compareFile.getFileName());
    		}
    	}
	}
	
	public Stream<Path> listFiles() throws IOException {
		return Files.walk(getPath(), 1).filter(path -> !path.equals(getPath())).sorted();
	}

}
