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
package com.b2international.rf2.spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.b2international.rf2.Constants;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.model.RF2Release;
import com.b2international.rf2.naming.RF2ContentFileName;
import com.b2international.rf2.naming.RF2DirectoryName;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.RF2ReleaseName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;

/**
 * Describes an RF2 Release, its structure, versioning, naming, etc. in the form of an {@value #RF2_SPEC_YML} file.
 * @since 0.3
 */
public final class RF2Specification {

	private static final String RF2_SPEC_YML = "rf2-spec.yml";

	private static final ObjectMapper MAPPER;
	static {
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
		MAPPER = mapper;
	}

	private final String version;
	private final String rf2Version;
	private final RF2ReleaseSpecification release;

	@JsonCreator
	public RF2Specification(@JsonProperty("version") String version, @JsonProperty("rf2Version") String rf2Version,
			@JsonProperty("release") RF2ReleaseSpecification release) {
		this.version = version;
		this.rf2Version = rf2Version;
		this.release = release;
	}

	/**
	 * @return version of the {@value #RF2_SPEC_YML} file model.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the RF2 specification version used in the {@value #RF2_SPEC_YML} file.
	 */
	public String getRf2Version() {
		return rf2Version;
	}

	/**
	 * @return the release specification
	 */
	public RF2ReleaseSpecification getRelease() {
		return release;
	}

	/**
	 * Merge the given {@link RF2Specification} with this {@link RF2Specification} and return the end result as a new {@link RF2Specification}
	 * instance.
	 * 
	 * @param other
	 * @return
	 */
	public RF2Specification merge(RF2Specification other) {
		return new RF2Specification(
			Optional.ofNullable(other.version).orElse(version), 
			Optional.ofNullable(other.rf2Version).orElse(rf2Version), 
			release.merge(other.release)
		);
	}
	
	/**
	 * Detects an {@link RF2File} from the given file path using this {@link RF2ReleaseSpecification}
	 * 
	 * @param path
	 *            - the file path to recognize
	 * @return an {@link RF2File} instance
	 */
	@SuppressWarnings("unchecked")
	public <T extends RF2File> T detect(final Path path) {
		if (!Files.exists(path)) {
			throw new IllegalArgumentException(String.format("'%s' path argument does not exist.", path));
		}
		
		String fileName = path.getFileName().toString();
		RF2FileName rf2Release = null;
		// directories are always recognized and accepted
		if (Files.isDirectory(path)) {
			rf2Release = new RF2DirectoryName(fileName);
		} else {
			// if we are not in the OS file system, then in case of zip files we treat them as regular files
			if (path.toUri().getScheme().equals("jar")) {
				rf2Release = new RF2ContentFileName(fileName);
			} else {
				// if it is not a directory and we are in the OS file system then try to parse the fileName as an RF2Release
				if (fileName.endsWith(RF2FileName.FILE_EXT_SEPARATOR + RF2File.ZIP)) {
					rf2Release = new RF2ReleaseName(fileName);
				}
				if (rf2Release == null || rf2Release.isUnrecognized()) {
					// if it is not a release package fall back and treat it as an RF2 File (in general any file can be part of a release)
					rf2Release = new RF2ContentFileName(fileName);
				}
			}
		}
		
		return (T) rf2Release.createRF2File(path.getParent(), this);
	}
	
	/**
	 * Prepare and return an {@link RF2Release} based on this {@link RF2Specification}.
	 * 
	 * @param parent
	 * @return
	 */
	public RF2Release prepare(Path parent) {
		final String releaseName = String.format("%s_%s%s_%s_%sT%sZ.%s", release.getInitial(), release.getProduct(), release.getFormat(), release.getStatus(), release.getDate(), release.getTime(), Constants.ZIP);
		return new RF2ReleaseName(releaseName).createRF2File(parent, this);
	}

	/**
	 * Reads the default {@value #RF2_SPEC_YML} file from the classpath and returns it as {@link RF2Specification} instance.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static RF2Specification readDefault() throws IOException {
		return MAPPER.readValue(Resources.getResource(RF2Specification.class, "/" + RF2_SPEC_YML), RF2Specification.class);
	}

	/**
	 * Reads an {@value #RF2_SPEC_YML} file from the specified path and returns it as an {@link RF2Specification} instance.
	 * 
	 * @param path
	 *            - the path pointing to an {@value #RF2_SPEC_YML} file.
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static RF2Specification readFrom(Path path) throws IOException {
		Preconditions.checkArgument(path.endsWith(RF2_SPEC_YML), "Path must point to an '%s' file.", RF2_SPEC_YML);
		Preconditions.checkArgument(Files.exists(path), "Path to '%s' file does not exist.", RF2_SPEC_YML);
		return MAPPER.readValue(path.toFile(), RF2Specification.class);
	}

	/**
	 * Reads and returns the current {@link RF2Specification} instance that must be used by all rf2 subcommands. Reads the default
	 * {@value #RF2_SPEC_YML} from the classpath and merges it with the user specified {@value #RF2_SPEC_YML} from the current working directory, if
	 * exists.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static RF2Specification get(Path workDir) throws IOException {
		// load default from classpath
		RF2Specification spec = readDefault();

		// load custom user specified rf2-spec file if exists and merge with default
		final Path userSpec = workDir.resolve(RF2_SPEC_YML);
		if (Files.exists(userSpec)) {
			spec = spec.merge(readFrom(userSpec));
		}

		return spec;
	}

}
