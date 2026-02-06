/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.DatabindException;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatcherPropertiesUpdate {

    @Nullable
    private Boolean controller;

    @Nullable
    private Boolean restController;

    @Nullable
    private Boolean service;

    @Nullable
    private Boolean repository;

    @Nullable
    private Boolean component;

    @Nullable
    private Boolean restTemplate;

    @Nullable
    private Boolean webClient;

    @Nullable
    private Boolean actuatorHealth;

    @Nullable
    private List<String> beans;

    @Nullable
    private List<Class<?>> beanClasses;

    public void applyTo(WatcherProperties t) {
        try {
            new ObjectMapper().updateValue(t, this);
        } catch (DatabindException e) {
            throw new IllegalArgumentException("cannot update values", e);
        }
    }
}
