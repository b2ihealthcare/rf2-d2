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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.rf2.model.RF2Directory;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.module.RF2ModuleGraph;
import com.b2international.rf2.spec.RF2ReleaseSpecification;
import com.b2international.rf2.spec.RF2Specification;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 0.1
 */
@Command(
	name = "create",
	description = "Creates an RF2 Release from a set of RF2 files and/or archives"
)
public final class RF2Create extends RF2Command {
	
	private static final String OUTDIR_DESCRIPTION = "Output directory where the RF2 Release will be created.";
	private static final String PATH_DESCRIPTION = "RF2 source files to use when creating the RF2 Release.";
	private static final String PRODUCT_DESCRIPTION = "Configure the [Product] value in the name of the created RF2 Release. Default value is empty.";
	private static final String RELEASE_STATUS_DESCRIPTION = "Configure the [ReleaseStatus] value in the name of the created RF2 Release. Default value is 'PRODUCTION'.";
	private static final String RELEASE_DATE_DESCRIPTION = "Configure the [ReleaseDate] value in the name of the created RF2 Release. Default value is today's date.";
	private static final String RELEASE_TIME_DESCRIPTION = "Configure the [ReleaseTime] value in the name of the created RF2 Release. Default value is the current time.";
	private static final String COUNTRY_DESCRIPTION = "Configure the country value in the [CountryNamespace] part of RF2 Release files. Default value is 'INT'.";
	private static final String NAMESPACE_DESCRIPTION = "Configure the namespace value in the [CountryNamespace] part of RF2 Release files. Default value is empty.";
	private static final String CONTENT_SUB_TYPES_DESCRIPTION = "Configure the content sub types to be created in the RF2 Release. Default is ['Delta', 'Snapshot', 'Full'].";
	
	@Parameters(arity = "0..*", paramLabel = "PATH", description = PATH_DESCRIPTION, converter = RF2FileTypeConverter.class)
	List<RF2File> sources;
	
	@Option(required = false, names = {"-o", "--outdir"}, description = OUTDIR_DESCRIPTION)
	String outDir = "target";
	
	@Option(required = false, names = {"-p", "--product"}, description = PRODUCT_DESCRIPTION)
	String product;
	
	@Option(required = false, names = {"-s", "--status"}, description = RELEASE_STATUS_DESCRIPTION)
	String releaseStatus;
	
	@Option(required = false, names = {"-d", "--date"}, description = RELEASE_DATE_DESCRIPTION)
	String releaseDate;
	
	@Option(required = false, names = {"-t", "--time"}, description = RELEASE_TIME_DESCRIPTION)
	String releaseTime;
	
	@Option(required = false, names = {"-c", "--country"}, description = COUNTRY_DESCRIPTION)
	String country;
	
	@Option(required = false, names = {"-n", "--namespace"}, description = NAMESPACE_DESCRIPTION)
	String namespace;
	
	@Option(required = false, names = {"-C", "--contentsubtype"}, description = CONTENT_SUB_TYPES_DESCRIPTION)
	String[] contentSubTypes;
	
	@Override
	public void doRun(RF2Specification specification) throws Exception {
		final Path outputDirectory;
		if (new File(outDir).isAbsolute()) {
			outputDirectory = Paths.get(outDir);
		} else {
			outputDirectory = WORK_DIR.resolve(outDir);
		}
		
		if (sources != null) {
			List<RF2File> directories = sources.stream()
					.filter(RF2Directory.class::isInstance)
					.collect(Collectors.toList());
			
			if (!directories.isEmpty()) {
				directories.forEach(source -> {
					console.log("Only .txt and .zip files are accepted as RF2 source files. '%s' is a directory.", source.getPath());
				});
				return;
			}
		} else {
			sources = Collections.emptyList();
		}
		
		Files.createDirectories(outputDirectory);

		// load RF2 specification
		RF2Specification mergedSpec = specification
				// merge overridable options from command line
				.merge(new RF2Specification(null, null, new RF2ReleaseSpecification(null, product, null, releaseStatus, country, namespace, releaseDate, releaseTime, contentSubTypes, null)));

		final RF2ModuleGraph moduleGraph = new RF2ModuleGraph();
		mergedSpec
			.prepare(outputDirectory)
			.create(new RF2CreateContext(mergedSpec, sources, moduleGraph, console));
		System.err.println("Module graph: " + moduleGraph.getModuleDependencies());
	}

}
