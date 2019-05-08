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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.naming.RF2DirectoryName;
import com.b2international.rf2.naming.RF2ReleaseName;
import com.b2international.rf2.spec.RF2ContentFileSpecification;
import com.b2international.rf2.spec.RF2ReleaseSpecification;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1 
 */
public final class RF2Release extends RF2File {

	private final RF2Specification specification;

	public RF2Release(Path parent, RF2ReleaseName fileName, RF2Specification specification) {
		super(parent, fileName);
		this.specification = specification;
	}
	
	@Override
	public void visit(Consumer<RF2File> visitor) throws IOException {
		if (!Files.exists(getPath())) {
			throw new IllegalStateException("Cannot visit non-existing RF2 Release: " + getPath());
		}
		
		visitor.accept(this);
		try (FileSystem zipfs = openZipfs(false)) {
			for (Path root : zipfs.getRootDirectories()) {
				Files.walk(root, 1).forEach(path -> {
					if (!RF2Directory.ROOT_PATH.equals(path.toString())) {
						try {
							specification.detect(path).visit(visitor);
						} catch (IOException e) {
							throw new RuntimeException("Couldn't visit path: " + path, e);
						}
					}
				});
			}
		}
	}
	
	private FileSystem openZipfs(boolean create) throws IOException {
		return FileSystems.newFileSystem(URI.create("jar:" + getPath().toUri()), Map.of("create", String.valueOf(create)));
	}
	
	@Override
	public String getType() {
		return "Release";
	}

	@Override
	public void create(RF2CreateContext context) throws IOException {
		if (Files.exists(getPath())) {
			throw new IllegalStateException("Cannot overwrite and create RF2 Release at path: " + getPath());
		}

		final RF2Specification specification = context.getSpecification();
		
		try (FileSystem zipfs = openZipfs(true)) {
			// root folder with same name
			RF2Directory rootDir = new RF2DirectoryName(getRF2FileName().getFileName()).createRF2File(zipfs.getPath("/"), specification);
			rootDir.create(context);

			RF2ReleaseSpecification release = specification.getRelease();
			for (String contentSubType : release.getContentSubTypes()) {
				RF2Directory contentSubTypeDir = new RF2DirectoryName(contentSubType).createRF2File(rootDir.getPath(), specification);
				contentSubTypeDir.create(context);
				
				for (Entry<String, List<RF2ContentFileSpecification>> entry : release.getContent().getFiles().entrySet()) {
					RF2Directory rf2Directory = new RF2DirectoryName(entry.getKey()).createRF2File(contentSubTypeDir.getPath(), specification);
					entry.getValue()
						.stream()
						.filter(RF2ContentFileSpecification::isDataFile)
						.forEach(file -> {
							try {
								rf2Directory.create(context);
								file.prepare(rf2Directory.getPath(), release, contentSubType)
									.create(context);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						});
				}
			}
			
			// create all non-data files outside of the contentSubType directories
			for (Entry<String, List<RF2ContentFileSpecification>> entry : release.getContent().getFiles().entrySet()) {
				RF2Directory rf2Directory = new RF2DirectoryName(entry.getKey()).createRF2File(rootDir.getPath(), specification);
				entry.getValue()
					.stream()
					.filter(Predicate.not(RF2ContentFileSpecification::isDataFile))
					.forEach(file -> {
						try {
							rf2Directory.create(context);
							file.prepare(rf2Directory.getPath(), release, null /*use specification based contentSubType*/)
								.create(context);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					});
			}
			
		}
		context.log("Created RF2 release at %s", getPath());
	}
	
}
