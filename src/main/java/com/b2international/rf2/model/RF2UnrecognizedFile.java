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
package com.b2international.rf2.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public final class RF2UnrecognizedFile extends RF2File {

	public RF2UnrecognizedFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	@Override
	public void visit(Consumer<RF2File> visitor) {
		visitor.accept(this);
	}
	
	@Override
	public void create(RF2CreateContext context) throws IOException {
		context.warn("Creating unrecognized files like '%s' is not supported.", getRF2FileName());
	}

    @Override
    public void transform(RF2TransformContext context) throws IOException {
	    // In case of unrecognized files transform is essentially a copy
        Files.copy(getPath(), getRF2FileName().createRF2File(context.getParent(), context.getSpecification()).getPath());
        context.log("Copied unrecognized file '%s'", getPath());
    }

	@Override
	public String getType() {
		return "Unrecognized";
	}
	
}
