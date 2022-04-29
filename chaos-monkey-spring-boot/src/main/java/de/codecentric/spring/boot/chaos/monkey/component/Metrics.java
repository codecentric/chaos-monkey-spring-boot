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
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationListener;

/** @author Benjamin Wilms */
public class Metrics implements ApplicationListener<MetricEvent> {

    private MeterRegistry meterRegistry;

    public Metrics() {
        this.meterRegistry = io.micrometer.core.instrument.Metrics.globalRegistry;
    }

    private void counter(MetricType type, String... tags) {
        if (meterRegistry != null && tags != null) {
            meterRegistry.counter(type.getMetricName(), tags).increment();
        }
    }

    private void counterWatcher(MetricType type, String name) {
        if (meterRegistry != null) {
            meterRegistry.counter(type.getMetricName() + ".watcher", "component", extractComponent(name)).increment();
        }
    }

    private void gauge(MetricType type, double number) {
        if (meterRegistry != null) {
            meterRegistry.gauge(type.getMetricName() + ".gauge.", number);
        }
    }

    private String extractComponent(String name) {
        return name.replaceAll("execution.", "");
    }

    @Override
    public void onApplicationEvent(MetricEvent event) {
        if (event.getMetricType().isSignatureOnlyEvent()) {
            counterWatcher(event.getMetricType(), event.getMethodSignature());
        } else if (event.getMetricType().isTagEvent()) {
            counter(event.getMetricType(), event.getTags());
        } else { // untagged and without method signature -> metric value is the interesting
            // part
            gauge(event.getMetricType(), event.getMetricValue());
            counter(event.getMetricType(), event.getTags());
        }
    }
}
