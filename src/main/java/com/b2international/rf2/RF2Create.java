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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.model.RF2Release;

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
	
	private final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
	
	@Parameters(arity = "1", paramLabel = "OUTDIR", description = OUTDIR_DESCRIPTION, index = "0")
	String outDir;
	
	@Parameters(arity = "0..*", paramLabel = "PATH", description = PATH_DESCRIPTION, index = "1..*")
	List<String> paths;
	
	@Option(required = false, names = {"-p", "--product"}, description = PRODUCT_DESCRIPTION)
	String product = "";
	
	@Option(required = false, names = {"-s", "--status"}, description = RELEASE_STATUS_DESCRIPTION)
	String releaseStatus = "PRODUCTION";
	
	@Option(required = false, names = {"-d", "--date"}, description = RELEASE_DATE_DESCRIPTION)
	String releaseDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);
	
	@Option(required = false, names = {"-t", "--time"}, description = RELEASE_TIME_DESCRIPTION)
	String releaseTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
	
	@Option(required = false, names = {"-c", "--country"}, description = COUNTRY_DESCRIPTION)
	String country = "INT";
	
	@Option(required = false, names = {"-n", "--namespace"}, description = NAMESPACE_DESCRIPTION)
	String namespace = "";
	
	@Option(required = false, names = {"-C", "--contentsubtype"}, description = CONTENT_SUB_TYPES_DESCRIPTION)
	String[] contentSubTypes = new String[] {"Delta", "Snapshot", "Full"};
	
	@Override
	public void run() {
		final Path parent = Paths.get(outDir);
		if (!Files.isDirectory(parent)) {
			console.log("Output directory '%s' does not exist or is not a directory.", outDir);
			return;
		}

		final List<RF2File> sources;
		if (paths != null) {
			sources = paths.stream().map(path -> RF2File.<RF2File>detect(path)).collect(Collectors.toList());
		} else {
			sources = Collections.emptyList();
		}
		
		RF2Release release = RF2Release.create(parent, product, releaseStatus, releaseDate, releaseTime);
		try {
			release.create(new RF2CreateContext(contentSubTypes, releaseDate, country, namespace, sources, console));
			console.log("Created RF2 release at %s", release.getPath());
		} catch (Exception e) {
			console.log(e.getMessage());
		}
	}

}
