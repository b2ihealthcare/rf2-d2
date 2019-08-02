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
package com.b2international.rf2.module;

import com.b2international.rf2.naming.file.RF2ContentType;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @since 0.4.0
 */
public class RF2ModuleGraph {

    private static final long MODEL_COMPONENT_MODULE = 900000000000012004L;

    private final PrimitiveLongMultimap moduleToEffectiveTimes = new PrimitiveLongMultimap();
    private final PrimitiveLongMultimap moduleToDependencies = new PrimitiveLongMultimap();
    private final Long2ObjectMap<PrimitiveLongMultimap> graphPerEffectiveTime = new Long2ObjectOpenHashMap<>();
    private final Long2LongMap idToModuleDependency = new Long2LongOpenHashMap();
    private final PrimitiveLongMultimap moduleDependencies = new PrimitiveLongMultimap();
    private final Long2ObjectMap<PrimitiveLongMultimap> moduleDependenciesPerEffectiveTime = new Long2ObjectOpenHashMap();

    private boolean isDirty;

    public synchronized void add(String[] line, Collection<String> dependencies, String fileType) {
            final long effectiveTime = Long.parseLong(line[1]);
            final long moduleId = Long.parseLong(line[3]);
            isDirty = true;
            if (!RF2ContentType.isRefset(fileType)) {
                final long id = Long.parseLong(line[0]);
                synchronized (idToModuleDependency) {
                    idToModuleDependency.put(id, moduleId);
                }
            }
        synchronized (moduleToDependencies) {
            moduleToEffectiveTimes.put(moduleId, effectiveTime);
            for (String dependency : dependencies) {
                try {
                    final long dependencyL = Long.parseLong(dependency);
                    moduleToDependencies.put(moduleId, dependencyL);
                    if (graphPerEffectiveTime.containsKey(effectiveTime)) {
                        graphPerEffectiveTime.get(effectiveTime).put(moduleId, dependencyL);
                    } else {
                        final PrimitiveLongMultimap dependencyPrimitiveMap = new PrimitiveLongMultimap();
                        dependencyPrimitiveMap.put(moduleId, dependencyL);
                        graphPerEffectiveTime.put(effectiveTime, dependencyPrimitiveMap);
                    }
                } catch (NumberFormatException e) {
                    // Ignore exception
                }
            }
        }
    }

    public PrimitiveLongMultimap getModuleToEffectiveTimes() {
        return moduleToEffectiveTimes;
    }

    private void buildGraphs() {
        if (isDirty) {
            buildModuleDependencies();
            buildModuleDependenciesPerEffectiveTime();
            isDirty = false;
        }
    }

    private void buildModuleDependencies() {
        synchronized (moduleToDependencies) {
            for (long module : moduleToDependencies.keySet()) {
                final LongSet dependencies = moduleToDependencies.get(module);
                for (long dependencyId : dependencies) {
                    if (idToModuleDependency.containsKey(dependencyId)) {
                        final long dependencyModule = idToModuleDependency.get(dependencyId);
                        if (canAddDependency(module, dependencyModule)) {
                            moduleDependencies.put(module, dependencyModule);
                        }
                    }
                }
            }
        }
    }

    private void buildModuleDependenciesPerEffectiveTime() {
        synchronized (moduleDependenciesPerEffectiveTime) {
            for (long effectiveTime : graphPerEffectiveTime.keySet()) {
                for (long module : graphPerEffectiveTime.get(effectiveTime).keySet()) {
                    final LongSet dependencies = moduleToDependencies.get(module);
                    for (long dependencyId : dependencies) {
                        if (idToModuleDependency.containsKey(dependencyId)) {
                            final long dependencyModule = idToModuleDependency.get(dependencyId);
                            if (canAddDependency(module, dependencyModule)) {
                                if (moduleDependenciesPerEffectiveTime.containsKey(effectiveTime)) {
                                    moduleDependenciesPerEffectiveTime.get(effectiveTime).put(module, dependencyModule);
                                } else {
                                    final PrimitiveLongMultimap dependencyPrimitiveMap = new PrimitiveLongMultimap();
                                    dependencyPrimitiveMap.put(module, dependencyModule);
                                    moduleDependenciesPerEffectiveTime.put(effectiveTime, dependencyPrimitiveMap);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public LongSet get (String moduleId) {
        return get(Long.parseLong(moduleId));
    }

    public LongSet get(long moduleId) {
        buildGraphs();
        return moduleDependencies.get(moduleId);
    }

    public long getEarliestEffectiveTime(long moduleId) {
        long earliestEffectiveTime = Long.MAX_VALUE;
        for (Entry<Long, PrimitiveLongMultimap> entry : moduleDependenciesPerEffectiveTime.entrySet()) {
            final Long effectiveTime = entry.getKey();
            final PrimitiveLongMultimap dependencies = entry.getValue();
            if ((dependencies.containsKey(moduleId) || dependencies.containsValue(moduleId)) && effectiveTime < earliestEffectiveTime) {
                earliestEffectiveTime = effectiveTime;
            }
        }

        return earliestEffectiveTime;
    }

    public long getEarliestDependency(long sourceModuleId, long targetModuleId) {
        long earliestEffectiveTime = Long.MAX_VALUE;
        for (Entry<Long, PrimitiveLongMultimap> entry : moduleDependenciesPerEffectiveTime.entrySet()) {
            final Long effectiveTime = entry.getKey();
            final PrimitiveLongMultimap dependencies = entry.getValue();
            if (dependencies.containsEntry(sourceModuleId, targetModuleId) && effectiveTime < earliestEffectiveTime) {
                earliestEffectiveTime = effectiveTime;
            }
        }

        return earliestEffectiveTime;
    }

    public long getLatestDependency(long sourceModuleId, long targetModuleId) {
        long latestEffectiveTIme = Long.MAX_VALUE;
        for (Entry<Long, PrimitiveLongMultimap> entry : moduleDependenciesPerEffectiveTime.entrySet()) {
            final Long effectiveTime = entry.getKey();
            final PrimitiveLongMultimap dependencies = entry.getValue();
            if (dependencies.containsEntry(sourceModuleId, targetModuleId) && effectiveTime > latestEffectiveTIme) {
                latestEffectiveTIme = effectiveTime;
            }
        }

        return latestEffectiveTIme;
    }

    public long getLatestEffectiveTime(long moduleId) {
        long latestEffectiveTIme = Long.MAX_VALUE;
        for (Entry<Long, PrimitiveLongMultimap> entry : moduleDependenciesPerEffectiveTime.entrySet()) {
            final Long effectiveTime = entry.getKey();
            final PrimitiveLongMultimap dependencies = entry.getValue();
            if ((dependencies.containsKey(moduleId) || dependencies.containsValue(moduleId)) && effectiveTime > latestEffectiveTIme) {
                latestEffectiveTIme = effectiveTime;
            }
        }

        return latestEffectiveTIme;
    }

    public PrimitiveLongMultimap getGraphForEffectiveTime(String effectiveTime) {
        return getGraphForEffectiveTime(Long.parseLong(effectiveTime));
    }

    public PrimitiveLongMultimap getGraphForEffectiveTime(long effectiveTime) {
        buildGraphs();
        return moduleDependenciesPerEffectiveTime.get(effectiveTime);
    }

    private boolean canAddDependency(long module, long dependencyModule) {
        return module != dependencyModule && MODEL_COMPONENT_MODULE != module;
    }

    public boolean remove(long sourceModuleId, long targetModuleId) {
        buildGraphs();
       return moduleDependencies.remove(sourceModuleId, targetModuleId);
    }
}
