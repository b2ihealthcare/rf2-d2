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
import it.unimi.dsi.fastutil.longs.*;

import java.util.Collection;

/**
 * @since 0.4.0
 */
public class RF2ModuleGraph {

    private final Long2LongMap moduleToEffectiveTime = new Long2LongOpenHashMap();
    private final PrimitiveLongMultimap moduleToDependencies = new PrimitiveLongMultimap();
    private final Long2ObjectMap<PrimitiveLongMultimap> graphPerEffectiveTime = new Long2ObjectOpenHashMap<>();
    private final Long2LongMap idToModuleDependency = new Long2LongOpenHashMap();

    public synchronized void add(String[] line, Collection<String> dependencies, String fileType) {
            final long effectiveTime = Long.parseLong(line[1]);
            final long moduleId = Long.parseLong(line[3]);
            updateNodeEffectiveTime(moduleId, effectiveTime);

            if (!RF2ContentType.isRefset(fileType)) {
                final long id = Long.parseLong(line[0]);
                synchronized (idToModuleDependency) {
                    idToModuleDependency.put(id, moduleId);
                }
            }

        synchronized (moduleToDependencies) {
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

    private void updateNodeEffectiveTime(Long moduleId, Long newEffectiveTime) {
        synchronized (moduleToEffectiveTime) {
            if (moduleToEffectiveTime.containsKey(moduleId)) {
                final Long oldEffectiveTime = moduleToEffectiveTime.get(moduleId);
                if (oldEffectiveTime.compareTo(newEffectiveTime) == -1) {
                    moduleToEffectiveTime.put(moduleId, newEffectiveTime);
                }
            }
        }
    }

    public PrimitiveLongMultimap getModuleDependencies() {
        synchronized (moduleToDependencies) {
            final PrimitiveLongMultimap moduleDependencies = new PrimitiveLongMultimap();
            for (long module : moduleToDependencies.keySet()) {
                final LongSet dependencies = moduleToDependencies.get(module);
                for (long dependencyId : dependencies) {
                    if (idToModuleDependency.containsKey(dependencyId)) {
                        final long dependencyModule = idToModuleDependency.get(dependencyId);
                        if (module != dependencyModule) {
                            moduleDependencies.put(module, dependencyModule);
                        }
                    }
                }
            }
            return moduleDependencies;
        }

    }

    public Long2LongMap getModuleToEffectiveTime() {
        return moduleToEffectiveTime;
    }

    public void getGraphPerEffectiveTime() {
        for (long effectiveTime : graphPerEffectiveTime.keySet()) {
            final PrimitiveLongMultimap moduleDependencies = new PrimitiveLongMultimap();
            for (long module : graphPerEffectiveTime.get(effectiveTime).keySet()) {
                final LongSet dependencies = moduleToDependencies.get(module);
                for (long dependencyId : dependencies) {
                    if (idToModuleDependency.containsKey(dependencyId)) {
                        final long dependencyModule = idToModuleDependency.get(dependencyId);
                        if (module !=dependencyModule) {
                            moduleDependencies.put(module, dependencyModule);

                        }
                    }
                }
            }

            System.err.println("effectiveTime: " + effectiveTime + "\nmodule dependencies for effectiveTime: " + moduleDependencies);
        }
    }

}
