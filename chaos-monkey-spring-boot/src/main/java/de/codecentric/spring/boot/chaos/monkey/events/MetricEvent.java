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
package de.codecentric.spring.boot.chaos.monkey.events;

import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.ApplicationEvent;

/** @author Benjamin Wilms */
public class MetricEvent extends ApplicationEvent {

    private final MetricType metricType;

    private final double metricValue;

    private final String methodSignature;

    private final String[] tags;

    /**
     * Create a new Chaos Monkey for Spring Boot Metric ApplicationEvent.
     *
     * @param source
     *            the object on which the event initially occurred (never
     *            {@code null})
     * @param metricType
     *            MetricType
     * @param methodSignature
     *            String
     * @param tags
     *            String[]
     */
    public MetricEvent(Object source, MetricType metricType, String methodSignature, String... tags) {
        this(source, metricType, -1, methodSignature, tags);
    }

    public MetricEvent(Object source, MetricType metricType, String... tags) {
        this(source, metricType, -1, null);
    }

    public MetricEvent(Object source, MetricType metricType, long metricValue, String methodSignature, String... tags) {
        super(source);
        this.metricType = metricType;
        this.tags = tags;
        this.methodSignature = methodSignature;
        this.metricValue = metricValue;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String[] getTags() {
        return tags;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public AtomicInteger getGaugeValue() {
        return new AtomicInteger((int) metricValue);
    }
}
