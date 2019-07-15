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

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.io.Serializable;
import java.util.Map;

/**
 * @ 0.4.0
 */
public class PrimitiveLongMultimap implements Serializable {

    private final Long2ObjectMap<LongSet> primitiveMultimap = new Long2ObjectOpenHashMap<>();
    private int totalSize;

    public PrimitiveLongMultimap() {
        this.totalSize = 0;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(long key) {
        return primitiveMultimap.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code LongSets.EMPTY_SET} if this map contains no mapping for the key.
     * @param key
     * @return a {$Link LongSet} associated with this key
     */
    public LongSet get(long key) {
        return containsKey(key) ? primitiveMultimap.get(key) : LongSets.EMPTY_SET;
    }

    /**
     * Returns a {@link LongSet} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.
     *
     * @return a set view of the keys contained in this map
     */
    public LongSet keySet() {
        return primitiveMultimap.keySet();
    }


    /**
     * Stores a key-value pair in the primitive multimap.
     *
     * @param key key to store in the primitive multimap
     * @param value value to store in the primitive multimap
     */
    public void put(long key, long value) {
        if (containsKey(key)) {
            get(key).add(value);
            totalSize++;
        } else {
            final LongSet longSet = new LongOpenHashSet();
            longSet.add(value);
            primitiveMultimap.put(key, longSet);
            totalSize++;
        }
    }

    /**
     * Returns the number of key-value pairs in this primitive multimap.
     */
    public int size() {
        return totalSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final ObjectSet<Map.Entry<Long, LongSet>> entries = primitiveMultimap.entrySet();
        for (Map.Entry<Long, LongSet> entry : entries) {
            final Long key = entry.getKey();
            final LongSet values = entry.getValue();
            sb.append(key + " -> [");

            for (long value : values) {
                sb.append(value + ", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
