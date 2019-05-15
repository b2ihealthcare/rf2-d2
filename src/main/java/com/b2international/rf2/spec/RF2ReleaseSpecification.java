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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 0.3
 */
public final class RF2ReleaseSpecification {


    private final String initial;
    private final String product;
    private final String format;
    private final String status;
    private final String country;
    private final String namespace;
    private final String date;
    private final String time;
    private final String[] contentSubTypes;
    private final RF2ReleaseContent content;
    private final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

    @JsonCreator
    public RF2ReleaseSpecification(
    	@JsonProperty("initial") final String initial,
		@JsonProperty("product") final String product,
		@JsonProperty("format") final String format,
		@JsonProperty("status") final String status,
		@JsonProperty("country") final String country,
		@JsonProperty("namespace") final String namespace,
		@JsonProperty("date") final String date,
		@JsonProperty("time") final String time,
		@JsonProperty("contentSubType") final String[] contentSubTypes,
		@JsonProperty("content") final RF2ReleaseContent content) {
		this.initial = initial;
		this.product = product;
		this.format = format;
		this.status = status;
		this.country = country;
		this.namespace = namespace;
        this.date = date;
        this.time = time;
		this.contentSubTypes = contentSubTypes;
		this.content = content;
	}
    
    public String getInitial() {
		return initial;
	}
    
    public String getProduct() {
        return product;
    }
    
    public String getFormat() {
		return format;
	}

    public String getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public String getNamespace() {
        return namespace;
    }
    
    public String getDate() {
        return Strings.isNullOrEmpty(date) ? now.format(DateTimeFormatter.BASIC_ISO_DATE) : date;
    }

    public String getTime() {
        return Strings.isNullOrEmpty(time) ? now.format(DateTimeFormatter.ofPattern("HHmmss")) : time;
    }

    @JsonProperty("contentSubType")
    public String[] getContentSubTypes() {
        return contentSubTypes;
    }

    public RF2ReleaseContent getContent() {
        return content;
    }
    
    public RF2ReleaseSpecification merge(RF2ReleaseSpecification other) {
    	return new RF2ReleaseSpecification(
    		Optional.ofNullable(other.initial).orElse(initial), 
    		Optional.ofNullable(other.product).orElse(product), 
    		Optional.ofNullable(other.format).orElse(format), 
    		Optional.ofNullable(other.status).orElse(status), 
    		Optional.ofNullable(other.country).orElse(country), 
    		Optional.ofNullable(other.namespace).orElse(namespace), 
    		Optional.ofNullable(other.date).orElse(date), 
    		Optional.ofNullable(other.time).orElse(time), 
    		Optional.ofNullable(other.contentSubTypes).orElse(contentSubTypes), 
    		content == null ? other.content : content.merge(other.content)
    	);
    }

}
