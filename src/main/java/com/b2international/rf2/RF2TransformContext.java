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

import com.b2international.rf2.spec.RF2Specification;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.nio.file.Path;

/**
 * @since 0.3
 */
public final class RF2TransformContext {

    private final RF2Specification specification;
    private final Path parent;
    private final Console console;

    private Script compiledScript;

    public RF2TransformContext(String rawScript, RF2Specification specification, Path parent, Console console) {
        this(compileScript(rawScript), specification, parent, console);
    }

    RF2TransformContext(Script compiledScript, RF2Specification specification, Path parent,  Console console) {
        this.specification = specification;
        this.parent = parent;
        this.compiledScript = compiledScript;
        this.console = console;
    }

    private static Script compileScript(String rawScript) {
        final GroovyShell shell = new GroovyShell();
        try {
           return (Script) shell.getClassLoader().parseClass(rawScript).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't instantiate script", e);
        }

    }

    public void log(String message, Object... args) {
        console.log(message, args);
    }

    public void warn(String message, Object... args) {
        console.warn(message, args);
    }

    public void error(String message, Object... args) {
        console.error(message, args);
    }

    public RF2Specification getSpecification() {
        return specification;
    }

    public Path getParent() {
        return parent;
    }

    public RF2TransformContext newSubContext(Path parent) {
        return new RF2TransformContext(compiledScript, specification, parent, console);
    }

    public Script getCompiledScript() {
        return compiledScript;
    }

}
