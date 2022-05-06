/*
 * Copyright 2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChaosTarget {
    CONTROLLER("controller", MetricType.CONTROLLER),
    REST_CONTROLLER("restController", MetricType.RESTCONTROLLER),
    REPOSITORY("repository", MetricType.REPOSITORY),
    COMPONENT("component", MetricType.COMPONENT),
    SERVICE("service", MetricType.SERVICE),
    REST_TEMPLATE("restTemplate", null),
    WEB_CLIENT("webClient", null),
    ACTUATOR_HEALTH("actuatorHealth", null),
    BEAN("bean", MetricType.BEAN);

    @Getter
    private final String name;
    @Getter
    private final MetricType metricType;
}
