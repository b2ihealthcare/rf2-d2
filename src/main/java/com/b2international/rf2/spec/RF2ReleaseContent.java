/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

/**
 * @since 0.3
 */
@JsonDeserialize(using = RF2ReleaseContent.RF2ReleaseContentDeserializer.class)
public final class RF2ReleaseContent {

    private final Map<String, List<RF2ContentFileSpecification>> files;

    public RF2ReleaseContent(Map<String, List<RF2ContentFileSpecification>> files) {
        this.files = ImmutableMap.copyOf(files);
    }

    public Map<String, List<RF2ContentFileSpecification>> getFiles() {
        return files;
    }

    public Stream<RF2ContentFileSpecification> fileSpecifications() {
    	return files.values().stream().flatMap(List::stream);
    }
    
    public RF2ReleaseContent merge(RF2ReleaseContent other) {
    	if (other == null) return this;
    	final ImmutableMap.Builder<String, List<RF2ContentFileSpecification>> builder = ImmutableMap.builder();
    	
    	MapDifference<String, List<RF2ContentFileSpecification>> diff = Maps.difference(files, other.files);
    	
    	// add all duplicate, and single side entries
    	diff.entriesInCommon().entrySet().forEach(builder::put);
    	diff.entriesOnlyOnLeft().entrySet().forEach(builder::put);
    	diff.entriesOnlyOnRight().entrySet().forEach(builder::put);
    	// merge differing entries 
    	for (Entry<String, ValueDifference<List<RF2ContentFileSpecification>>> entry : diff.entriesDiffering().entrySet()) {
    		final String key = entry.getKey();
    		final Map<RF2ContentFileKey, RF2ContentFileSpecification> newValues = Maps.newHashMap();
    		processContentFileSpecifications(entry.getValue().leftValue(), newValues);
    		processContentFileSpecifications(entry.getValue().rightValue(), newValues);
    		builder.put(key, ImmutableList.copyOf(newValues.values()));
    	}
    	
		return new RF2ReleaseContent(builder.build());
	}
    
    private void processContentFileSpecifications(List<RF2ContentFileSpecification> specs, Map<RF2ContentFileKey, RF2ContentFileSpecification> newValues) {
    	for (RF2ContentFileSpecification spec : specs) {
    		newValues.merge(new RF2ContentFileKey(spec.getContentType(), spec.getHeader()), spec, RF2ContentFileSpecification::merge);
		}
	}

	static final class RF2ContentFileKey {
    	
    	private final String contentType;
		private final String[] header;

		public RF2ContentFileKey(String contentType, String[] header) {
			this.contentType = contentType;
			this.header = header;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(contentType, Arrays.hashCode(header));
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			RF2ContentFileKey other = (RF2ContentFileKey) obj;
			return Objects.equals(contentType, other.contentType)
					&& Arrays.equals(header, other.header);
		}
    	
    }

    static final class RF2ReleaseContentDeserializer extends JsonDeserializer<RF2ReleaseContent> {

        @Override
        public RF2ReleaseContent deserialize(JsonParser p, DeserializationContext context) throws IOException {
            final JsonNode node = p.readValueAsTree();
            return new RF2ReleaseContent(deserializeSpecifications(node));
        }

        private Map<String, List<RF2ContentFileSpecification>> deserializeSpecifications(JsonNode node) {
            final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            final Map<String, List<RF2ContentFileSpecification>> fileSpecificationsByContent = Maps.newHashMap();

            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final JsonNode value = field.getValue();
                if ("files".equals(field.getKey())) {
                	final List<RF2ContentFileSpecification> fileSpecificationList = new ObjectMapper().convertValue(value, new TypeReference<List<RF2ContentFileSpecification>>(){});
                	fileSpecificationsByContent.put(field.getKey(), fileSpecificationList);
                } else {
                	final Map<String, List<RF2ContentFileSpecification>> subFileSpecificationsByContent = deserializeSpecifications(value);
                    for (String key : subFileSpecificationsByContent.keySet()) {
                        final List<RF2ContentFileSpecification> fileSpecifications = subFileSpecificationsByContent.get(key);
                        // files directory should be attached to the parent directory
                        if ("files".equals(key)) {
                        	fileSpecificationsByContent.put(field.getKey(), fileSpecifications);
                        } else {
                        	fileSpecificationsByContent.put(String.format("%s%s%s", field.getKey(), File.separator, key), fileSpecifications);
                        }
                    }
                }
            }

            return fileSpecificationsByContent;
        }

    }

}
