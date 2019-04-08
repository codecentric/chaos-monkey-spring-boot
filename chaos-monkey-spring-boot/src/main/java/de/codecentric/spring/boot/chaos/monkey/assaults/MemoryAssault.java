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

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRuntimeScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Benjamin Wilms
 */
public class MemoryAssault implements ChaosMonkeyRuntimeAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAssault.class);
    private static AtomicLong stolenMemory = new AtomicLong(0);

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
        return settings.getAssaultProperties().isMemoryActive();
    }

    @Override @Async
    public void attack() {
        LOGGER.info("Chaos Monkey - memory assault");

        // metrics
        if (metricEventPublisher != null)
            metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT);

        eatFreeMemory();

        LOGGER.info("Chaos Monkey - memory assault cleaned up");
    }

    private void eatFreeMemory() {
        int minimumFreeMemoryPercentage = calculatePercentIncreaseValue(settings.getAssaultProperties().getMemoryMinFreePercentage());

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        Vector<byte[]> memoryVector = new Vector<>();
        long stolenHere = 0L;
        int percentIncreaseValue = calculatePercentIncreaseValue(calculatePercentageRandom());

        while (isActive() && runtime.freeMemory() >= minimumFreeMemoryPercentage && runtime.freeMemory() > percentIncreaseValue) {

            // only if ChaosMonkey in general is enabled, triggers a stop if the attack is canceled during an experiment
                // increase memory random percent steps
                byte[] b = new byte[percentIncreaseValue];
                // touch the memory to actually make the OS commit it
                ThreadLocalRandom.current().nextBytes(b);

                stolenHere += percentIncreaseValue;
                long newStolenTotal = stolenMemory.addAndGet(percentIncreaseValue);

                metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT_MEMORY_STOLEN, newStolenTotal);
                memoryVector.add(b);

                LOGGER.debug("Chaos Monkey - memory assault increase, free memory: " + runtime.freeMemory());

                waitUntil(settings.getAssaultProperties().getMemoryMillisecondsWaitNextIncrease());
                percentIncreaseValue = calculatePercentIncreaseValue(settings.getAssaultProperties().getMemoryFillPercentage());
        }

        // Hold memory level and cleanUp after, only if experiment is running
        if (isActive()) {
            waitUntil(settings.getAssaultProperties().getMemoryMillisecondsHoldFilledMemory());
        }

        // clean Vector
        memoryVector.clear();
        // quickly run gc for reuse
        Runtime.getRuntime().gc();

        long stolenAfterComplete = stolenMemory.addAndGet(-stolenHere);
        metricEventPublisher.publishMetricEvent(MetricType.MEMORY_ASSAULT_MEMORY_STOLEN, stolenAfterComplete);
    }

    private int calculatePercentIncreaseValue(double percentage) {
        return (int) Math.max(1, runtime.freeMemory() * percentage);
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
