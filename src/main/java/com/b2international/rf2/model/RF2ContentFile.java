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
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.b2international.rf2.Constants;
import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.check.RF2IssueAcceptor;
import com.b2international.rf2.module.PrimitiveLongMultimap;
import com.b2international.rf2.module.RF2ModuleGraph;
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
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;

import groovy.lang.Binding;
import groovy.lang.Script;
import it.unimi.dsi.fastutil.longs.LongSet;


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
        try {
            return Files.lines(path).findFirst().orElse("N/A").split(TAB);
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

    private boolean isModuleDependencyFile() {
        return specification.isModuleDependencyFile();
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
            checkDependencies(actualHeader, specification.getDependencies(), acceptor);

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

    private void checkDependencies(String[] actualHeader, String[] dependencies, RF2IssueAcceptor acceptor) {
        Sets.difference(Set.of(dependencies), Set.of(actualHeader)).forEach(diff -> acceptor.error("Differing header found in dependencies: '%s'.", diff));
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
	                            newDataLine[i] = String.valueOf(params.get(header[i]));
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

            final Predicate<String[]> lineFilter = getLineFilter();
            final RF2ModuleGraph moduleGraph = context.getModuleGraph();

            context.visitSourceRows(this::fileFilter, lineFilter, /* parallel if */ releaseType.isSnapshot(), (file, line) -> {
                try {
                	// this will initialize the map with 0 counter values, just to register all files even if they are empty, so we will log all applicable files during the process
                	copiedLinesPerFile.merge(file.getPath().toString(), 0, Integer::sum);

                	collectModuleDependencies(line, context);
                    final String id = line[0];
                    final String effectiveTime = line[1];
                    String rawLine = newLine(line);
                    final String lineHash = Hashing.sha256().hashString(rawLine, StandardCharsets.UTF_8).toString();

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
                        // In case of module dependency file apply special logic to writing
                        if (isModuleDependencyFile()) {
                            final long sourceModuleId = Long.parseLong(line[3]);
                            final long targetModuleId = Long.parseLong(line[5]);
                            final long sourceEffectiveTime = Long.parseLong(line[6]);
                            final long targetEffectiveTime = Long.parseLong(line[7]);
                            final LongSet calculatedTargetModuleIds = moduleGraph.get(sourceModuleId);
                            if (calculatedTargetModuleIds.contains(targetModuleId)) {
                                final PrimitiveLongMultimap moduleDependenciesForEffectiveTime = moduleGraph.getGraphForEffectiveTime(effectiveTime);
                                final LongSet dependencies = moduleDependenciesForEffectiveTime.get(sourceModuleId);
                                if (dependencies.contains(targetModuleId)) {
                                    final long latestEffectiveTime = moduleGraph.getLatestDependency(sourceModuleId, targetModuleId);
                                    final long earliestEffectiveTime = moduleGraph.getEarliestDependency(sourceModuleId, targetModuleId);
                                    tryFixEffectiveTime(rawLine, context, sourceEffectiveTime, targetEffectiveTime, latestEffectiveTime, earliestEffectiveTime);
                                    writer.write(rawLine);
                                    // this will increase the number of copied lines by 1
                                    copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                                } else {
                                    context.log("FULL release is missing dependency pair source: '%s' target: '%s' in effective time: '%s'", sourceModuleId, targetModuleId, effectiveTime);
                                }
                            } else {
                                context.log("Extra dependency found in FULL file. Skipping... '%s'", line);
                            }
                        } else {
                            writer.write(rawLine);
                            // this will increase the number of copied lines by 1
                            copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                        }

                    } else if (releaseType.isSnapshot()) {
                        // in case of Snapshot we check that the current effective time is greater than the currently registered and replace if yes
                        if (componentsByIdEffectiveTime.containsKey(id)) {
                            final Entry<String, String> effectiveTimeHash = Iterables.getOnlyElement(componentsByIdEffectiveTime.get(id).entrySet());
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
                            // In case of module dependency file apply special logic to writing
                            if (isModuleDependencyFile()) {
                                final long sourceModuleId = Long.parseLong(line[3]);
                                final long targetModuleId = Long.parseLong(line[5]);
                                final long sourceEffectiveTime = Long.parseLong(line[6]);
                                final long targetEffectiveTime = Long.parseLong(line[7]);
                                final LongSet calculatedTargetModuleIds = moduleGraph.get(sourceModuleId);
                                if (calculatedTargetModuleIds.contains(targetModuleId)) {
                                    final long latestEffectiveTime = moduleGraph.getLatestDependency(sourceModuleId, targetModuleId);
                                    final long earliestEffectiveTime = moduleGraph.getEarliestDependency(sourceModuleId, targetModuleId);
                                    tryFixEffectiveTime(rawLine, context, sourceEffectiveTime, targetEffectiveTime, latestEffectiveTime, earliestEffectiveTime);
                                    writer.write(rawLine);
                                } else {
                                    // add new row with known current releaseDate
                                    final String missingLine = buildModuleDependencyLine(moduleGraph, context.getSpecification().getRelease().getDate(), sourceModuleId, targetModuleId);
                                    writer.write(missingLine);
                                    context.log("Added missing module dependency row to delta file '%s'", missingLine);
                                }
                            } else {
                                writer.write(rawLine);
                            }

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
                        final String id = line[0];
                        final String effectiveTime = line[1];
                        if (componentsByIdEffectiveTime.containsKey(id) && componentsByIdEffectiveTime.get(id).containsKey(effectiveTime)) {
                            // remove the item from the id effective time map to indicate that we wrote it out
                            componentsByIdEffectiveTime.remove(id);
                            // In case of module dependency file apply special logic to writing
                            String newLine = newLine(line);

                            if (isModuleDependencyFile()) {
                                final long sourceModuleId = Long.parseLong(line[3]);
                                final long targetModuleId = Long.parseLong(line[5]);
                                final long sourceEffectiveTime = Long.parseLong(line[6]);
                                final long targetEffectiveTime = Long.parseLong(line[7]);
                                final LongSet calculatedTargetModuleIds = moduleGraph.get(sourceModuleId);
                                if (calculatedTargetModuleIds.contains(targetModuleId)) {
                                    final long latestEffectiveTime = moduleGraph.getLatestDependency(sourceModuleId, targetModuleId);
                                    final long earliestEffectiveTime = moduleGraph.getEarliestDependency(sourceModuleId, targetModuleId);
                                    tryFixEffectiveTime(newLine, context, sourceEffectiveTime, targetEffectiveTime, latestEffectiveTime, earliestEffectiveTime);
                                    copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                                    writer.write(newLine);
                                } else {
                                    context.log("Extra dependency found in snapshot file. Skipping... '%s'", line);
                                }

                            } else {
                                copiedLinesPerFile.merge(file.getPath().toString(), 1, Integer::sum);
                                writer.write(newLine);
                            }
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

    private void tryFixEffectiveTime(String newLine, RF2CreateContext context, long sourceEffectiveTime, long targetEffectiveTime, long latestEffectiveTime, long earliestEffectiveTime) {
        final String oldLine = newLine;
        if (latestEffectiveTime >= earliestEffectiveTime) {
            if (latestEffectiveTime != sourceEffectiveTime) {
                // change source effectiveTime to latest
                newLine.replaceAll(Long.toString(sourceEffectiveTime), Long.toString(latestEffectiveTime));
                context.log("Changed source effective time from '%s' to '%s' for line: %s", sourceEffectiveTime, latestEffectiveTime, oldLine);
            }

            if (earliestEffectiveTime != targetEffectiveTime) {
                // change target effectiveTime to earliest
                newLine.replaceAll(Long.toString(targetEffectiveTime), Long.toString(earliestEffectiveTime));
                context.log("Changed target effective time from '%s' to '%s' for line: %s", targetEffectiveTime, earliestEffectiveTime, oldLine);
            }
        }
    }

    private String buildModuleDependencyLine(RF2ModuleGraph moduleGraph, String effectiveTime, long sourceModuleId, long targetModuleId) {
        final String id = UUID.randomUUID().toString();
        final boolean active = true;
        final String refSetId = Constants.MODULE_DEPENDENCY_REFSET_ID;
        final String referencedComponentId = String.valueOf(targetModuleId);
        final String sourceEffectiveTime = String.valueOf(moduleGraph.getLatestEffectiveTime(sourceModuleId));
        final String targetEffectiveTime = String.valueOf(moduleGraph.getEarliestEffectiveTime(targetModuleId));

        return String.format("%s%s", String.join(TAB, id, String.valueOf(active), effectiveTime, refSetId, referencedComponentId, sourceEffectiveTime, targetEffectiveTime), CRLF);
    }

    private void collectModuleDependencies(String[] line, RF2CreateContext context) {
        final String[] dependencies = specification.getDependencies();
        if (dependencies == null) {
            return;
        }

        final Map<String, Integer> indexesByHeader = getIndexByHeader();
        final Set<String> dependencyIds = Sets.newHashSet();
        for (String dependencyHeader : dependencies) {
            dependencyIds.add(line[indexesByHeader.get(dependencyHeader)]);
        }

        context.getModuleGraph().add(line, dependencyIds, getType());
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
        // check actual content type as well, to copy content from the right files, filter module dependency file
        return Arrays.equals(file.getHeader(), getHeader());
    }

    private String newLine(String[] values) {
        return String.format("%s%s", String.join(TAB, values), CRLF);
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

}
