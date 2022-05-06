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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

class SpringBootHealthIndicatorAdviceIntegrationTest {

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.actuator-health=true",
            "chaos.monkey.assaults.exceptions-active=true"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class HealthIndicatorEnabledIntegrationTest {

        @Autowired
        private List<HealthIndicator> healthIndicators;

        @Test
        public void testIndicatorsAreDown() {
            this.healthIndicators.forEach(healthIndicator -> {
                assertThat(healthIndicator.getHealth(Boolean.TRUE).getStatus()).isEqualTo(Health.down().build().getStatus());
            });
        }
    }

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.actuator-health=false",
            "chaos.monkey.assaults.exceptions-active=true"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class HealthIndicatorDisabledIntegrationTest {

        @Autowired
        private List<HealthIndicator> healthIndicators;

        @Test
        public void testIndicatorsAreUp() {
            this.healthIndicators.forEach(healthIndicator -> {
                assertThat(healthIndicator.getHealth(Boolean.TRUE).getStatus()).isEqualTo(Health.up().build().getStatus());
            });
        }
    }
}
