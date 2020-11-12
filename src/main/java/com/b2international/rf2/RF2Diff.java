/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @since 0.4.0
 */
@Command(
	name = "diff",
	description = "Diff two RF2 archives or files and display the differences in a close to similar format as usual diff softwares (+/- rows)."
)
public final class RF2Diff extends RF2Command {

	private static final String BASE_DESCRIPTION = "RF2 archive or file to compare the compare RF2 archive or file argument against.";
	private static final String COMPARE_DESCRIPTION = "RF2 archive or file to compare against the base RF2 archive or file argument.";
	
	@Parameters(arity = "1", paramLabel = "BASE", description = BASE_DESCRIPTION, index = "0", converter = RF2FileTypeConverter.class)
	RF2File base;
	
	@Parameters(arity = "1", paramLabel = "COMPARE", description = COMPARE_DESCRIPTION, index = "1", converter = RF2FileTypeConverter.class)
	RF2File compare;
	
	@Override
	protected void doRun(RF2Specification specification) throws Exception {
		compare.diff(base, console);
	}

}
