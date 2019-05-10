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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.naming.RF2ContentFileName;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2VersionDate;
import com.b2international.rf2.spec.RF2ContentFileSpecification;
import com.b2international.rf2.spec.RF2Filter;
import com.b2international.rf2.validation.RF2ColumnValidator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * @since 0.1
 */
public final class RF2ContentFile extends RF2File {

	private final RF2ContentFileSpecification specification;
	private String[] header;
	
	public RF2ContentFile(Path parent, RF2ContentFileName fileName, RF2ContentFileSpecification specification) {
		super(parent, fileName);
		this.specification = specification;
	}
	
	@Override
	public String getType() {
		return getRF2FileName()
				.getElement(RF2ContentType.class)
				.map(RF2ContentType::getContentType)
				// TODO recognize type from header
				.orElse("Unknown");
	}
	
	@Override
	public void visit(Consumer<RF2File> visitor) {
		visitor.accept(this);
	}
	
	public boolean isDataFile() {
		return specification.isDataFile();
	}
	
	@Override
	public void check(RF2IssueAcceptor acceptor) throws IOException {
		super.check(acceptor);
		if (isDataFile()) {
			// check RF2 header
			final String[] rf2HeaderSpec = specification.getHeader();
			final String[] actualHeader = getHeader();
			if (!Arrays.equals(rf2HeaderSpec, actualHeader)) {
				// TODO report incorrect header columns
				acceptor.error("Header does not conform to specification");
				return;
			}
			
			// assign validators to RF2 columns
			final Map<Integer, RF2ColumnValidator> validatorsByIndex = new HashMap<>(actualHeader.length);
			for (int i = 0; i < actualHeader.length; i++) {
				final String columnHeader = actualHeader[i];
				final RF2ColumnValidator validator = RF2ColumnValidator.VALIDATORS.get(columnHeader);
				if (validator != null) {
					validatorsByIndex.put(i, validator);
				} else {
					acceptor.warn("No validator is registered for column header '%s'.", columnHeader);
					validatorsByIndex.put(i, RF2ColumnValidator.NOOP);
				}
			}
			// validate each row in RF2 content file

			rowsParallel().forEach(row -> {
				for (int i = 0; i < row.length; i++) {
					validatorsByIndex.get(i).check(this, actualHeader[i], row[i], acceptor);
				}
			});
		}
	}
	
	@Override
	public void create(RF2CreateContext context) throws IOException {
		context.log("Creating '%s'...", getPath());
		
		if (isDataFile()) {
			createDataFile(context);
		} else {
			final Ordering<RF2VersionDate> ordering = Ordering.natural().nullsFirst();
			final AtomicReference<RF2FileName> matchingSourceFile = new AtomicReference<>();
			final AtomicReference<RF2VersionDate> maxVersionDate = new AtomicReference<>();
			final AtomicBoolean exit = new AtomicBoolean(false);
			context.getSources()
					.stream()
					.takeWhile(file -> exit.get())
					.forEach(source -> {
						try {
							source.visit(file -> {
								if (getType().equals(file.getType())) {
									final RF2FileName rf2FileName = file.getRF2FileName();
									final RF2VersionDate newMaxVersionDate = ordering.max(maxVersionDate.get(), rf2FileName.getElement(RF2VersionDate.class).orElse(null));
									if (newMaxVersionDate != maxVersionDate.get()) {
										exit.set(true);
										matchingSourceFile.set(rf2FileName);
										maxVersionDate.set(newMaxVersionDate);
									}
								}
							});
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});

			if (matchingSourceFile.get() == null) {
				Files.createFile(getPath());

			} else {
				for (RF2File source : context.getSources()) {
					source.visit(file -> {
						if (matchingSourceFile.get().equals(file.getRF2FileName())) {
							try {
								Files.copy(file.getPath(), getPath());
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					});
				}
			}
		}
	}

	@Override
	public void transform(RF2TransformContext context) throws IOException {
		final Script compiledScript = context.getCompiledScript();
		final RF2File contentFile = getRF2FileName().createRF2File(context.getParent(), context.getSpecification());

		if (isDataFile()) {
			final BufferedWriter writer = Files.newBufferedWriter(contentFile.getPath(), StandardOpenOption.CREATE_NEW);
			writer.write(newLine(getHeader()));

			// In case of data file run the script on source
			rows().forEach(line -> {
				final Map<String, Object> params = Maps.newHashMap();
				params.put("_file", this);

				final String[] header = getHeader();
				for (int i = 0; i < line.length ; i++) {
					params.put(header[i], line[i]);
				}

				final Binding binding = new Binding(params);
				compiledScript.setBinding(binding);
				final Object returnValue = compiledScript.run();

				final boolean include = returnValue instanceof Boolean ? (boolean) returnValue : true;
				if (include) {

					final String[] newLine = new String[header.length];
					for (int i = 0; i < header.length; i++) {
						newLine[i] = String.valueOf(params.get(header[i]));
					}

					try {
						writer.write(newLine(newLine));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} else {
			Files.copy(getPath(), contentFile.getPath());
		}
	}

	private void createDataFile(RF2CreateContext context) throws IOException {
		final RF2ContentSubType releaseType = getRF2FileName().getElement(RF2ContentSubType.class).orElse(null);
		final String currentReleaseDate = getRF2FileName().getElement(RF2VersionDate.class).map(RF2VersionDate::getVersionDate).orElse("N/A");
		try (BufferedWriter writer = Files.newBufferedWriter(getPath(), StandardOpenOption.CREATE_NEW)) {
			writer.write(newLine(getHeader()));
			
			final Map<String, Map<String, String>> componentsByIdEffectiveTime = new HashMap<>();
            Predicate<String[]> lineFilter = getLineFilter();


            context.visitSourceRows(this::fileFilter, lineFilter, /* parallel if */ releaseType.isSnapshot(), line -> {
				try {
					String id = line[0];
					String effectiveTime = line[1];
					String rawLine = newLine(line);
					String lineHash = Hashing.sha256().hashString(rawLine, StandardCharsets.UTF_8).toString();

					if (componentsByIdEffectiveTime.containsKey(id) && componentsByIdEffectiveTime.get(id).containsKey(effectiveTime)) {
						// log a warning about inconsistent ID-EffectiveTime content, keep the first occurrence of the line and skip the others
						if (!lineHash.equals(componentsByIdEffectiveTime.get(id).get(effectiveTime))) {
							context.warn("Skipping duplicate RF2 line found with same '%s' ID in '%s' effectiveTime but with different column values.", id, effectiveTime);
						}
						return;
					}

					if (releaseType.isFull()) {
						// in case of Full we can immediately write it out
						if (!componentsByIdEffectiveTime.containsKey(id)) {
							componentsByIdEffectiveTime.put(id, new HashMap<>());
						}
						componentsByIdEffectiveTime.get(id).put(effectiveTime, lineHash);
						writer.write(rawLine);
					} else if (releaseType.isSnapshot()) {
						// in case of Snapshot we check that the current effective time is greater than the currently registered and replace if yes
						if (componentsByIdEffectiveTime.containsKey(id)) {
							Entry<String, String> effectiveTimeHash = Iterables.getOnlyElement(componentsByIdEffectiveTime.get(id).entrySet());
							if (effectiveTime.isEmpty() || effectiveTime.compareTo(effectiveTimeHash.getKey()) > 0) {
								componentsByIdEffectiveTime.put(id, Map.of(effectiveTime, lineHash));
							}
						} else {
							componentsByIdEffectiveTime.put(id, Map.of(effectiveTime, lineHash));	
						}
					} else if (releaseType.isDelta()) {
						// in case of Delta we will only add the lines with the releaseDate effective time
						// TODO support closest to specified releaseDate!!!
						if (currentReleaseDate.equals(effectiveTime)) {
							componentsByIdEffectiveTime.put(id, Map.of(effectiveTime, lineHash));
							writer.write(rawLine);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			
			// Snapshot needs a second run, since we just extracted the applicable rows from all source files, and we need to actually write them into the output
			if (releaseType.isSnapshot()) {
				context.visitSourceRows(this::fileFilter, lineFilter, false, line -> {
					try {
						String id = line[0];
						String effectiveTime = line[1];
						if (componentsByIdEffectiveTime.containsKey(id) && componentsByIdEffectiveTime.get(id).containsKey(effectiveTime)) {
							// remove the item from the id effective time map to indicate that we wrote it out
							componentsByIdEffectiveTime.remove(id);
							writer.write(newLine(line));
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}
		}
	}
	
	private Predicate<String[]> getLineFilter() {
		// TODO apply description type based
		final List<Predicate<String[]>> lineFilters = Lists.newArrayList();

		if (specification.getInclusions() != null) {
			lineFilters.addAll(createInclusions());
		}

		if (specification.getExclusions() != null) {
			lineFilters.addAll(createExclusions());
		}

		return line -> lineFilters.stream().allMatch(filter -> filter.test(line));
	}

	private List<Predicate<String[]>> createInclusions() {
		final Map<String, Integer> indexByHeader = getIndexByHeader();
		final List<Predicate<String[]>> inclusions = Lists.newArrayList();

		for (RF2Filter inclusion : specification.getInclusions()) {
			for (Entry<String, String> filterEntry : inclusion.getFilters().entrySet()) {
				final String fieldName = filterEntry.getKey();
				final String inclusionValue = filterEntry.getValue();
				if (indexByHeader.containsKey(fieldName)) {
					final int headerIndex = indexByHeader.get(fieldName).intValue();
					inclusions.add(line -> inclusionValue.equals(line[headerIndex]));
				}
			}
		}

		return inclusions;
	}

	private List<Predicate<String[]>> createExclusions() {
		final List<Predicate<String[]>> exclusions = Lists.newArrayList();
		final Map<String, Integer> indexByHeader = getIndexByHeader();

		for (RF2Filter inclusion : specification.getExclusions()) {
			for (Entry<String, String> filterEntry : inclusion.getFilters().entrySet()) {
				final String fieldName = filterEntry.getKey();
				final String exclusionValue = filterEntry.getValue();
				if (indexByHeader.containsKey(fieldName)) {
					final int headerIndex = indexByHeader.get(fieldName).intValue();
					exclusions.add(line -> !exclusionValue.equals(line[headerIndex]));
				}
			}
		}

		return exclusions;
	}

	private boolean fileFilter(RF2ContentFile file) {
		final String sourceContentType = file.getType();
		switch (getType()) {
		// allow both Relationship/StatedRelationships files as sources for these types
		case "Relationship":
		case "StatedRelationship":
			return "StatedRelationship".equals(sourceContentType) || "Relationship".equals(sourceContentType); 
		default:
			if (!sourceContentType.equals(getType())) {
				return false;
			}
		}
		// check actual content type as well, to copy content from the right files
		if (!Arrays.equals(file.getHeader(), getHeader())) {
			return false;
		}
		
		return true;
	}
	
	protected final String newLine(String[] values) {
		return String.format("%s%s", String.join(TAB, values), CRLF);
	}

	/**
	 * @return the current RF2 header by reading the first line of the file or if this is a non-existing file returns the header from the spec for kind of RF2 files
	 * @throws IOException 
	 */
	public final String[] getHeader() {
		if (header == null) {
			header = extractHeader(getPath());
		}
		return header == null ? specification.getHeader() : header;
	}

	/**
	 * @return the current RF2 header indexes mapped by the header's name.
	 */
	public final Map<String, Integer> getIndexByHeader() {
		final Map<String, Integer> indexByHeader = Maps.newHashMap();
		final List<String> header = Arrays.asList(getHeader());

		for (int i = 0; i < header.size(); i++) {
			indexByHeader.put(header.get(i), i);
		}

		return indexByHeader;
	}

	/**
	 * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects in a sequential stream.
	 * @throws IOException
	 */
	public final Stream<String[]> rows() throws IOException {
		return Files.lines(getPath())
				.skip(1)
				.map(line -> line.split(TAB));
	}
	
	/**
	 * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects in a parallel stream.
	 * @throws IOException
	 */
	public final Stream<String[]> rowsParallel() throws IOException {
		return Files.lines(getPath())
				.skip(1)
				.parallel()
				.map(line -> line.split(TAB));
	}
	
	/**
	 * Extract the RF2 file header from the file specified the the given path. Returns <code>null</code> if the path does not exist.
	 * @param path
	 * @return
	 */
	public static String[] extractHeader(Path path) {
		if (!path.toString().endsWith(TXT) || !Files.exists(path)) {
			return null;
		}
		try {
			return Files.lines(path).findFirst().orElse("N/A").split(TAB);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't extract RF2 file header from path: " + path, e);
		}
	}

}
