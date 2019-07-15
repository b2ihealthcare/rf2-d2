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

import com.b2international.rf2.module.PrimitiveLongMultimap;
import com.b2international.rf2.module.RF2ModuleGraph;
import com.b2international.rf2.naming.file.RF2ContentType;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * @since 0.4.0
 */
public class RF2ModuleGraphTest {

    @Test
    public void testGraphValidity() {
        final RF2ModuleGraph moduleGraph = new RF2ModuleGraph();
        final String module5 = "5";
        final String module8 = "8";
        final String module2 = "2";
        /*id	effectiveTime	active	moduleId	definitionStatusId*/
        final String[][] lines = {
                {"1", "20190121", "1" , module2, "3"},
                {"3", "20190121", "1" , module5, "4"},
                {"4", "20190121", "1" , module2, "6"},
                {"6", "20190121", "1" , module8, "7"},
                {"7", "20190121", "1" , module5, "9"},
                {"9", "20190121", "1" , module2, "3"}
        };
        for (String[] line : lines) {
            moduleGraph.add(line, Set.of(line[4]), RF2ContentType.CONCEPT.getContentType());
        }

        final PrimitiveLongMultimap moduleDependencies = moduleGraph.getModuleDependencies();

        final LongSet expectedModule2Dependencies = new LongOpenHashSet();
        expectedModule2Dependencies.add(5);
        expectedModule2Dependencies.add(8);

        final LongSet expectedModule5Dependencies = new LongOpenHashSet();
        expectedModule5Dependencies.add(2);

        final LongSet expectedModule8Dependencies = new LongOpenHashSet();
        expectedModule8Dependencies.add(5);

        assertThat(moduleDependencies.get(Long.parseLong(module2))).isEqualTo(expectedModule2Dependencies);
        assertThat(moduleDependencies.get(Long.parseLong(module5))).isEqualTo(expectedModule5Dependencies);
        assertThat(moduleDependencies.get(Long.parseLong(module8))).isEqualTo(expectedModule8Dependencies);
    }

}

