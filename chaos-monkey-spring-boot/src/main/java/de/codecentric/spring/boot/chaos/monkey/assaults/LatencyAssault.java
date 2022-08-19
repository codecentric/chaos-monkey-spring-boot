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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Thorsten Deelmann */
public class LatencyAssault implements ChaosMonkeyRequestAssault {

    private static final Logger Logger = LoggerFactory.getLogger(LatencyAssault.class);

    private final ChaosMonkeySettings settings;

    private final ChaosMonkeyLatencyAssaultExecutor assaultExecutor;

    private MetricEventPublisher metricEventPublisher;

    private AtomicInteger atomicTimeoutGauge;

    public LatencyAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher, ChaosMonkeyLatencyAssaultExecutor executor) {
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
        this.atomicTimeoutGauge = new AtomicInteger(0);
        this.assaultExecutor = executor;
    }

    public LatencyAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this(settings, metricEventPublisher, new LatencyAssaultExecutor());
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isLatencyActive();
    }

    @Override
    public void attack() {
        Logger.debug("Chaos Monkey - timeout");

        atomicTimeoutGauge.set(determineLatency());

        // metrics
        if (metricEventPublisher != null) {
            metricEventPublisher.publishMetricEvent(MetricType.LATENCY_ASSAULT);
            metricEventPublisher.publishMetricEvent(MetricType.LATENCY_ASSAULT, atomicTimeoutGauge);
        }

        assaultExecutor.execute(atomicTimeoutGauge.get());
    }

    protected int determineLatency() {
        final int latencyRangeStart = settings.getAssaultProperties().getLatencyRangeStart();
        final int latencyRangeEnd = settings.getAssaultProperties().getLatencyRangeEnd();

        if (latencyRangeStart == latencyRangeEnd) {
            return latencyRangeStart;
        } else {
            return ThreadLocalRandom.current().nextInt(latencyRangeStart, latencyRangeEnd);
        }
    }
}
