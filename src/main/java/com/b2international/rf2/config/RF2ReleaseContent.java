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
package com.b2international.rf2.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @since 0.3
 */
@JsonDeserialize(using = RF2ReleaseContent.RF2ReleaseContentDeserializer.class)
public final class RF2ReleaseContent {

    private Map<String, List<RF2FileSpecification>> fileSpecificationsByContent = Maps.newHashMap();

    public RF2ReleaseContent(Map<String, List<RF2FileSpecification>> fileSpecificationsByContent) {
        this.fileSpecificationsByContent = fileSpecificationsByContent;
    }

    public Map<String, List<RF2FileSpecification>> getFileSpecificationsByContent() {
        return fileSpecificationsByContent;
    }

    static final class RF2ReleaseContentDeserializer extends JsonDeserializer<RF2ReleaseContent> {

        @Override
        public RF2ReleaseContent deserialize(JsonParser p, DeserializationContext context) throws IOException {
            final ObjectCodec codec = p.getCodec();
            final JsonNode node = codec.readTree(p);

            return new RF2ReleaseContent(deserializeSpecifications(node));
        }

        private Map<String, List<RF2FileSpecification>> deserializeSpecifications(JsonNode node) {
            final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            final Map<String, List<RF2FileSpecification>> fileSpecificationsByContent = Maps.newHashMap();

            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final JsonNode value = field.getValue();
                if (value.isObject()) {
                    final Map<String, List<RF2FileSpecification>> subFileSpecificationsByContent = deserializeSpecifications(value);
                    for (String key : subFileSpecificationsByContent.keySet()) {
                        final List<RF2FileSpecification> fileSpecifications = subFileSpecificationsByContent.get(key);
                        fileSpecificationsByContent.put(String.format("%s/%s", field.getKey(), key), fileSpecifications);
                    }
                } else if (value.isArray()) {
                    // On this branch this is a specification it can safely be converted
                    final List<RF2FileSpecification> fileSpecificationList = new ObjectMapper().convertValue(value, new TypeReference<List<RF2FileSpecification>>(){});
                    fileSpecificationsByContent.put(field.getKey(), fileSpecificationList);
                }

            }

            return fileSpecificationsByContent;
        }

    }

}
