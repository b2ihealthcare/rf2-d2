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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.3
 */
public final class RF2ReleaseSpecification {

    private String product;
    private String releaseStatus;
    private String releaseDate;
    private String releaseTime;
    private String country;
    private String namespace;

    @JsonProperty("contentSubType")
    private String[] contentSubTypes;

    private RF2ReleaseContent content;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String[] getContentSubTypes() {
        return contentSubTypes;
    }

    public void setContentSubTypes(String[] contentSubTypes) {
        this.contentSubTypes = contentSubTypes;
    }

    public RF2ReleaseContent getContent() {
        return content;
    }

    public void setContent(RF2ReleaseContent content) {
        this.content = content;
    }

}
