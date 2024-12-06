/*
 * Copyright 2018-2024 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Thorsten Deelmann */
@ExtendWith(MockitoExtension.class)
class LatencyAssaultTest {

    @Test
    void threadSleepHasBeenCalled(@Mock MetricEventPublisher publisher) {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLatencyRangeStart(100);
        assaultProperties.setLatencyRangeEnd(200);
        ChaosMonkeySettings chaosMonkeySettings = new ChaosMonkeySettings();
        chaosMonkeySettings.setAssaultProperties(assaultProperties);

        TestLatencyAssaultExecutor executor = new TestLatencyAssaultExecutor();
        LatencyAssault latencyAssault = new LatencyAssault(chaosMonkeySettings, publisher, executor);
        latencyAssault.attack();

        assertTrue(executor.executed);
        assertTrue(executor.duration >= 100 && executor.duration <= 200, "Latency not in range 100-200, actual latency: " + executor.duration);
    }

    static class TestLatencyAssaultExecutor implements ChaosMonkeyLatencyAssaultExecutor {

        private long duration;

        private boolean executed;

        TestLatencyAssaultExecutor() {
            this.duration = 0;
            this.executed = false;
        }

        @Override
        public void execute(long duration) {
            this.duration = duration;
            this.executed = true;
        }
    }
}
