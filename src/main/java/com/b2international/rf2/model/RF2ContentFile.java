/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.console.Console;
import com.b2international.rf2.naming.RF2ContentFileName;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2VersionDate;
import com.b2international.rf2.spec.RF2ContentFileSpecification;
import com.b2international.rf2.spec.RF2Filter;
import com.b2international.rf2.validation.RF2ColumnValidator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;

import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * @since 0.1
 */
public final class RF2ContentFile extends RF2File {

    private static final Comparator<? super String[]> ROW_COMPARATOR = (left, right) -> ComparisonChain.start()
        		.compare(left[1], right[1]) // effectiveTime first
        		.compare(left[0], right[0]) // ID second
        		.result();
    
	private final RF2ContentFileSpecification specification;
    private String[] header;

    public RF2ContentFile(Path parent, RF2ContentFileName fileName, RF2ContentFileSpecification specification) {
        super(parent, fileName);
        this.specification = specification;
    }

    /**
     * Extract the RF2 file header from the file specified the the given path. Returns <code>null</code> if the path does not exist.
     *
     * @param path
     * @return
     */
    public static String[] extractHeader(Path path) {
        if (!path.toString().endsWith(TXT) || !Files.exists(path)) {
            return null;
        }
        try (Stream<String> line = Files.lines(path)) {
            return line.findFirst().orElse("N/A").split(TAB);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't extract RF2 file header from path: " + path, e);
        }
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
    	context.task("Creating file '%s'", getPath()).run(() -> {
    		if (isDataFile()) {
    			createDataFile(context);
    		} else {
    			final Ordering<RF2VersionDate> ordering = Ordering.natural().nullsFirst();
    			final AtomicReference<RF2FileName> matchingSourceFile = new AtomicReference<>();
    			final AtomicReference<RF2VersionDate> maxVersionDate = new AtomicReference<>();
    			context.getSources().forEach(source -> {
    				try {
    					source.visit(file -> {
    						if (getType().equals(file.getType())) {
    							final RF2FileName rf2FileName = file.getRF2FileName();
    							final RF2VersionDate newMaxVersionDate = ordering.max(maxVersionDate.get(), rf2FileName.getElement(RF2VersionDate.class).orElse(null));
    							if (newMaxVersionDate != maxVersionDate.get()) {
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
    	});
    }

    @Override
    public void transform(RF2TransformContext context) throws IOException {
    	final boolean isDataFile = isDataFile();
    	context.task(isDataFile ? "Transforming '%s'" : "Copying '%s'", getPath()).run(() -> {
    		final Script compiledScript = context.getCompiledScript();
	        final RF2File contentFile = getRF2FileName().createRF2File(context.getParent(), context.getSpecification());

	        if (isDataFile) {
	            int numberOfModifiedRows = 0;
	            int numberOfFilteredRows = 0;
	            int numberOfTotalRows = 0;
	            try (BufferedWriter writer = Files.newBufferedWriter(contentFile.getPath(), StandardOpenOption.CREATE_NEW)) {
	                writer.write(newLine(getHeader()));

	                // In case of data file run the script on source
	                for (String[] line : (Iterable<String[]>) rows()::iterator) {

	                    final Map<String, Object> params = Maps.newHashMap();
	                    params.put("_file", this);

	                    final String[] header = getHeader();
	                    if (header.length != line.length) {
	                    	context.warn("Incorrect number of columns in line: %s", Arrays.toString(line));
	                    	try {
	                    		// just write the line back as is
	                            writer.write(newLine(line));
	                        } catch (IOException e) {
	                            throw new RuntimeException(e);
	                        }
	                    	continue;
	                    }
	                    
	                    for (int i = 0; i < line.length; i++) {
                    		params.put(header[i], line[i]);
	                    }

	                    final Binding binding = new Binding(params);
	                    compiledScript.setBinding(binding);
	                    final Object returnValue = compiledScript.run();

	                    final boolean include = !(returnValue instanceof Boolean) || (boolean) returnValue;
	                    if (include) {

	                        final String[] newDataLine = new String[header.length];
	                        for (int i = 0; i < header.length; i++) {
	                        	Object newValue = params.get(header[i]);
	                            newDataLine[i] = newValue == null ? "" : String.valueOf(newValue);
	                        }
	                        if (!Arrays.equals(newDataLine, line)) {
	                            numberOfModifiedRows++;
	                        }


	                        try {
	                            writer.write(newLine(newDataLine));
	                        } catch (IOException e) {
	                            throw new RuntimeException(e);
	                        }
	                    } else {
	                        numberOfFilteredRows++;
	                    }
	                    numberOfTotalRows++;
	                }
	            }
	            context.log("Total lines: '%s'", numberOfTotalRows);

	            if (numberOfFilteredRows !=0) {
                    context.log("Excluded lines: '%s'", numberOfFilteredRows);
                }

	            if (numberOfModifiedRows !=0) {
                    context.log("Modified lines: '%s'", numberOfModifiedRows);
                }

	        } else {
	            Files.copy(getPath(), contentFile.getPath());
	        }    		
    	});
    }
    
    @Override
    public void diff(RF2File other, Console console) throws IOException {
    	Preconditions.checkArgument(other instanceof RF2ContentFile, "Cannot compare non-content RF2 file '%s' with '%s'", other.getPath(), getPath());
    	RF2ContentFile otherContentFile = (RF2ContentFile) other;
    	Preconditions.checkArgument(Arrays.equals(getHeader(), otherContentFile.getHeader()), "Cannot compare content files with different headers: '%s' vs '%s'", getHeader(), otherContentFile.getHeader());
    	Iterator<String[]> compareRows = sortedRows().iterator();
    	Iterator<String[]> baseRows = otherContentFile.sortedRows().iterator();

    	// since both streams are sorted, we can compare them line by line by iterating over both at the same time
    	String[] compareRow = compareRows.hasNext() ? compareRows.next() : null;
    	String[] baseRow = baseRows.hasNext() ? baseRows.next() : null;
    	while (compareRow != null && baseRow != null) {
    		if (!Arrays.equals(compareRow, baseRow)) {
    			// not equal lines
    			int compare = ROW_COMPARATOR.compare(compareRow, baseRow);
    			if (compare == 0) {
    				// same ID, effectiveTime, but different value somewhere, register both as +/-
    				console.log("-%s", line(baseRow));
    				console.log("+%s", line(compareRow));
    			} else if (compare < 0) {
    				// compare is earlier than base, compare values are missing from base
    				console.log("+%s", line(compareRow));
    				compareRow = compareRows.hasNext() ? compareRows.next() : null;
    			} else {
    				// compare is later than base, base values are missing from compare, proceed in base
    				console.log("-%s", line(baseRow));
    				baseRow = baseRows.hasNext() ? baseRows.next() : null;
    			}
    		} else {
    			// proceed in both streams
    			baseRow = baseRows.hasNext() ? baseRows.next() : null;
    			compareRow = compareRows.hasNext() ? compareRows.next() : null;
    		}
    	}

    	// if there are items in either of the streams, then register them as +/-
    	if (baseRow != null) {
    		console.log("-%s", line(baseRow));
    		while (baseRows.hasNext()) {
    			baseRow = baseRows.next();
    			console.log("-%s", line(baseRow));
    		}
    	}

    	if (compareRow != null) {
    		console.log("+%s", line(compareRow));
    		while (compareRows.hasNext()) {
    			compareRow = compareRows.next();
    			console.log("+%s", line(compareRow));
    		}
    	}

    }

    private void createDataFile(RF2CreateContext context) throws IOException {
        final RF2ContentSubType releaseType = getRF2FileName().getElement(RF2ContentSubType.class).orElse(null);
        final String currentReleaseDate = getRF2FileName().getElement(RF2VersionDate.class).map(RF2VersionDate::getVersionDate).orElse("N/A");
        try (BufferedWriter writer = Files.newBufferedWriter(getPath(), StandardOpenOption.CREATE_NEW)) {
            writer.write(newLine(getHeader()));

            final ConcurrentMap<String, Map<String, String>> componentsByIdEffectiveTime = new MapMaker()
            		.concurrencyLevel(Math.max(2, Runtime.getRuntime().availableProcessors()))
            		.makeMap();
            
            final ConcurrentMap<String, Integer> copiedLinesPerFile = new MapMaker()
            		.concurrencyLevel(Math.max(2, Runtime.getRuntime().availableProcessors()))
            		.makeMap();
            
            Predicate<String[]> lineFilter = getLineFilter();

            context.visitSourceRows(this::fileFilter, lineFilter, /* parallel if */ releaseType.isSnapshot(), (file, line) -> {
                try {
                	// this will initialize the map with 0 counter values, just to register all files even if they are empty, so we will log all applicable files during the process
                	copiedLinesPerFile.merge(file.getPath().toString(), 0, Integer::sum);
                	
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
                        // this will increase the number of copied lines by 1
                        copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
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
                            copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Snapshot needs a second run, since we just extracted the applicable rows from all source files, and we need to actually write them into the output
            if (releaseType.isSnapshot()) {
                context.visitSourceRows(this::fileFilter, lineFilter, false, (file, line) -> {
                    try {
                        String id = line[0];
                        String effectiveTime = line[1];
                        if (componentsByIdEffectiveTime.containsKey(id) && componentsByIdEffectiveTime.get(id).containsKey(effectiveTime)) {
                            // remove the item from the id effective time map to indicate that we wrote it out
                            componentsByIdEffectiveTime.remove(id);
                            writer.write(newLine(line));
                            copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            
            copiedLinesPerFile.keySet().stream().sorted().forEach(file -> {
            	context.log("Copied '%s' lines from '%s'", copiedLinesPerFile.get(file), file);
            });
            
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
                    final int headerIndex = indexByHeader.get(fieldName);
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
                    final int headerIndex = indexByHeader.get(fieldName);
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
        return Arrays.equals(file.getHeader(), getHeader());
    }

    private String newLine(String[] values) {
        return line(values).concat(CRLF);
    }

    private String line(String[] values) {
		return String.join(TAB, values);
	}

	/**
     * @return the current RF2 header by reading the first line of the file or if this is a non-existing file returns the header from the spec for kind of RF2 files
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
    private Map<String, Integer> getIndexByHeader() {
        final Map<String, Integer> indexByHeader = Maps.newHashMap();
        final String[] header = getHeader();
        for (int i = 0; i < header.length; i++) {
            indexByHeader.put(header[i], i);
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
                .map(line -> line.split(TAB, -1));
    }
    
    /**
	 * NOTE: this method does not read the entire source file into memory just by calling it, but when starting a terminal operation on the returned
	 * Stream, due to the sort's nature, it will load the entire file content into memory which might cause memory issues on certain environment or
	 * scenarios.
	 * 
	 * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects in a sequential stream
	 *         sorted by effectiveTime then by ID.
	 * @throws IOException
	 */
    public final Stream<String[]> sortedRows() throws IOException {
    	return rows().sorted(ROW_COMPARATOR);
    }

    /**
     * @return the actual raw data from this RF2 content file without header and each line converted into String[] objects in a parallel stream.
     * @throws IOException
     */
    public final Stream<String[]> rowsParallel() throws IOException {
        return Files.lines(getPath())
                .skip(1)
                .parallel()
                .map(line -> line.split(TAB, -1));
    }

}
