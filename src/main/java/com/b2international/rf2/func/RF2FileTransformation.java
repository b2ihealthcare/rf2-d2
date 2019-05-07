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
package com.b2international.rf2.func;

import java.io.IOException;
import java.nio.file.Path;

import com.b2international.rf2.Console;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.file.RF2ContentSubType;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @since 0.3
 */
public final class RF2FileTransformation {

	private final RF2File source;
	private final String script;

	public RF2FileTransformation(RF2File source, String script) {
		this.source = source;
		this.script = script;
	}
	
	public void writeTo(Path outputDirectory, Console log) throws IOException {
		final GroovyShell shell = new GroovyShell();
		final Script compiledScript; 
		try {
			compiledScript = (Script) shell.getClassLoader().parseClass(script).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Couldn't instantiate script", e);
		}
		
//		source.create(new RF2CreateContext(contentSubTypes, releaseDate, country, namespace, sources, log));
		source.visit(file -> {
			RF2FileName fileName = file.getRF2FileName();
			
			fileName.getElement(RF2ContentSubType.class).get().getReleaseType();
//			fileName.createRF2File(outputDirectory).create(new RF2CreateContext(contentSubTypes, releaseDate, country, namespace, sources, log));
		});
//		if (source instanceof RF2Release) {
//			
//		} else {
//		}
		
//		final Binding binding = new Binding(params);
//		compiledScript.setBinding(binding);
//		return (T) compiledScript.run();
	}
	
}
