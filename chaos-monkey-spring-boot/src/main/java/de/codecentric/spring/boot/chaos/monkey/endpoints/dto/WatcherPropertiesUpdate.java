/*
 * Copyright 2019-2022 the original author or authors.
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
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.List;
import java.util.function.Consumer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

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

    private <T> void applyTo(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public void applyTo(WatcherProperties t) {
        applyTo(controller, t::setController);
        applyTo(restController, t::setRestController);
        applyTo(service, t::setService);
        applyTo(repository, t::setRepository);
        applyTo(component, t::setComponent);
        applyTo(restTemplate, t::setRestTemplate);
        applyTo(webClient, t::setWebClient);
        applyTo(actuatorHealth, t::setActuatorHealth);
        applyTo(beans, t::setBeans);
    }
}
