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
 *
 */

package de.codecentric.spring.boot.chaos.monkey;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = ChaosDemoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("chaos-monkey")
class DefaultSettingsIntegrationTest {

  @Autowired private ChaosMonkeySettings monkeySettings;

  @Test
  void masterSwitchShouldDefaultToOff() {
    assertThat(monkeySettings.getChaosMonkeyProperties().isEnabled()).isFalse();
  }

  @Test
  void watchersShouldBeDisabledByDefault() {
    assertThat(monkeySettings.getAssaultProperties().getWatchedCustomServices()).isNull();
    assertThat(monkeySettings.getWatcherProperties().isController()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isRestController()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isRepository()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isService()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isComponent()).isFalse();
  }

  @Test
  void assaultsShouldBeDisabledByDefault() {
    assertThat(monkeySettings.getAssaultProperties().isLatencyActive()).isFalse();
    assertThat(monkeySettings.getAssaultProperties().isExceptionsActive()).isFalse();
  }

  @Test
  void levelShouldDefaultToOne() {
    assertThat(monkeySettings.getAssaultProperties().getLevel()).isEqualTo(1);
  }

  @Test
  void latencyDefaultsShouldBeSensible() {
    assertThat(monkeySettings.getAssaultProperties().getLatencyRangeStart()).isEqualTo(1000);
    assertThat(monkeySettings.getAssaultProperties().getLatencyRangeEnd()).isEqualTo(3000);
  }
}
