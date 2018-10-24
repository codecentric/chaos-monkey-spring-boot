/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Benjamin Wilms
 */
public class MemoryAssault implements ChaosMonkeyAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAssault.class);
    private Runtime runtime;
    private final ChaosMonkeySettings settings;
    private MetricEventPublisher metricEventPublisher;

    public MemoryAssault(Runtime runtime, ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.runtime = runtime;
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isExceptionsActive();
    }

    @Override
    public AssaultType getAssaultType() {
        return AssaultType.RUNTIME;
    }

    @Override
    public void attack() {
        LOGGER.info("Chaos Monkey - memory assault");

        // metrics
        if (metricEventPublisher != null)
            metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT);

        eatFreeMemory();

        LOGGER.info("Chaos Monkey - memory assault cleaned up");

    }

    private void eatFreeMemory() {

        long maxAvailableMemory = runtime.freeMemory();
        int minimumFreeMemoryPercentage = calculatePercentIncreaseValue(settings.getAssaultProperties().getMemoryMinFreePercentage());

        AtomicReference<Vector<byte[]>> memoryVector = new AtomicReference<>(new Vector<>());
        int percentIncreaseValue  = calculatePercentIncreaseValue(calculatePercentageRandom());

        while (runtime.freeMemory() >= minimumFreeMemoryPercentage && runtime.freeMemory() > percentIncreaseValue) {

            // increase memory random percent steps
            byte b[] = new byte[percentIncreaseValue];
            memoryVector.get().add(b);

            LOGGER.debug("Chaos Monkey - memory assault increase, free memory: " +runtime.freeMemory());

            waitUntil(settings.getAssaultProperties().getMemoryIncreaseLevel());
            percentIncreaseValue = calculatePercentIncreaseValue(settings.getAssaultProperties().getMemoryFillPercentage());
        }

        waitUntil(settings.getAssaultProperties().getMemoryKeepFilledLevel());

        // clean Vector
        memoryVector.get().clear();

        // quickly run gc for reuse
        Runtime.getRuntime().gc();

    }

    private int calculatePercentIncreaseValue(double percentage) {
        return (int) (runtime.freeMemory() * percentage);
    }

    private double calculatePercentageRandom() {
        return ThreadLocalRandom.current().nextDouble(0.05, settings.getAssaultProperties().getMemoryFillPercentage());
    }

    private void waitUntil(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
