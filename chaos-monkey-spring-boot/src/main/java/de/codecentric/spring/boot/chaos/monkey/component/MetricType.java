/*
 * Copyright 2018-2022 the original author or authors.
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

/** @author Benjamin Wilms */
public enum MetricType {
    CONTROLLER("controller", true, false),
    RESTCONTROLLER("restController", true, false),
    REPOSITORY("repository", true, false),
    COMPONENT("component", true, false),
    SERVICE("service", true, false),
    BEAN("bean", true, false),
    LATENCY_ASSAULT("assault.latency.count", false, false),
    EXCEPTION_ASSAULT("assault.exception.count", false, false),
    KILLAPP_ASSAULT("assault.killapp.count", false, true),
    APPLICATION_REQ_COUNT("application.request.count", false, true),
    MEMORY_ASSAULT("assault.memory.count", false, false),
    MEMORY_ASSAULT_MEMORY_STOLEN("assault.memory.bytes_stolen", false, false),
    CPU_ASSAULT("assault.cpu.count", false, false);

    private final String metricName;

    private final boolean signatureEvent;

    private final boolean tagEvent;

    MetricType(String metricName, boolean signatureEvent, boolean tagEvent) {
        this.metricName = metricName;
        this.signatureEvent = signatureEvent;
        this.tagEvent = tagEvent;
    }

    public String getMetricName() {
        String metricBaseName = "chaos.monkey.";
        return metricBaseName + metricName;
    }

    public boolean isSignatureOnlyEvent() {
        return signatureEvent;
    }

    public boolean isTagEvent() {
        return tagEvent;
    }
}
