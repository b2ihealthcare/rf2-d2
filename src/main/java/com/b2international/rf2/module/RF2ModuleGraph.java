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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

/**
 * @since 0.4.0
 */
public class RF2ModuleGraph {

    private final Map<String, String> moduleToEffectiveTime = Maps.newHashMap();
    private final Multimap<String, String> moduleToDependencies = HashMultimap.create();
    private final Map<String, String> idToModuleDependency = Maps.newHashMap();

    public synchronized void add(String[] line, Collection<String> dependencies, String fileType) {
            final String id = line[0];
            final String effectiveTime = line[1];
            final String moduleId = line[3];
            updateNodeEffectiveTime(moduleId, effectiveTime);

            if (!RF2ContentType.isRefset(fileType)) {
                synchronized (idToModuleDependency) {
                    idToModuleDependency.put(id, moduleId);
                }
            }

        synchronized (moduleToDependencies) {
            for (String dependency : dependencies) {
                moduleToDependencies.put(moduleId, dependency);
            }
        }
    }

    private void updateNodeEffectiveTime(String moduleId, String newEffectiveTime) {
        synchronized (moduleToEffectiveTime) {
            if (moduleToEffectiveTime.containsKey(moduleId)) {
                final String oldEffectiveTime = moduleToEffectiveTime.get(moduleId);
                if (oldEffectiveTime.compareTo(newEffectiveTime) == -1) {
                    moduleToEffectiveTime.put(moduleId, newEffectiveTime);
                }
            }
        }
    }

    public Multimap<String, String> getModuleDependencies() {
        synchronized (moduleToDependencies) {
            final Multimap<String, String> moduleDependencies = HashMultimap.create();
            for (String module : moduleToDependencies.keySet()) {
                final Collection<String> dependencies = moduleToDependencies.get(module);
                for (String dependencyId : dependencies) {
                    if (idToModuleDependency.containsKey(dependencyId)) {
                        final String dependencyModule = idToModuleDependency.get(dependencyId);
                        if (/*!moduleDependencies.containsEntry(module, dependencyModule) && !moduleDependencies.containsEntry(dependencyModule, module) &&*/ !module.equals(dependencyModule)) {
                            moduleDependencies.put(module, dependencyModule);
                        }
                    }
                }
            }
            return moduleDependencies;
        }

    }

    public Map<String, String> getModuleToEffectiveTime() {
        return moduleToEffectiveTime;
    }
}
