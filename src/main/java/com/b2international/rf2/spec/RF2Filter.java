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
package com.b2international.rf2.spec;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @since 0.3
 */
public class RF2Filter {

    private final Map<String, String> filters = Maps.newHashMap();

    @JsonAnySetter
    public void set(String key, Object value) {
        Preconditions.checkNotNull(value);
        if (value instanceof Long) {
            filters.put(key, String.valueOf(value));
        } else if(value instanceof String) {
            filters.put(key, (String) value);
        }
    }

    public Map<String, String> getFilters() {
        return filters;
    }

}
