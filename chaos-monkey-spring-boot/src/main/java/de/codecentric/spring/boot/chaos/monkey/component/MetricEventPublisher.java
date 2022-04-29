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

import de.codecentric.spring.boot.chaos.monkey.events.MetricEvent;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/** @author Benjamin Wilms */
public class MetricEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    public void publishMetricEvent(String signature, MetricType metricType, String... tags) {
        MetricEvent metricEvent = new MetricEvent(this, metricType, signature, tags);

        publisher.publishEvent(metricEvent);
    }

    public void publishMetricEvent(MetricType metricType, String... tags) {
        MetricEvent metricEvent = new MetricEvent(this, metricType, tags);

        publisher.publishEvent(metricEvent);
    }

    public void publishMetricEvent(MetricType metricType, AtomicInteger atomicTimeoutGauge) {
        final long gaugeValue = (atomicTimeoutGauge == null) ? -1 : atomicTimeoutGauge.longValue();
        MetricEvent metricEvent = new MetricEvent(this, metricType, gaugeValue, null);
        publisher.publishEvent(metricEvent);
    }

    public void publishMetricEvent(MetricType type, long metricValue) {
        publisher.publishEvent(new MetricEvent(this, type, metricValue, null));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
