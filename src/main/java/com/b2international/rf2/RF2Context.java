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

import com.b2international.rf2.console.Console;
import com.b2international.rf2.console.DelegatingConsole;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.3
 */
public abstract class RF2Context extends DelegatingConsole {

    protected final RF2Specification specification;

    public RF2Context(RF2Specification specification, Console console) {
    	super(console);
        this.specification = specification;
    }

    public RF2Specification getSpecification() {
        return specification;
    }

}
