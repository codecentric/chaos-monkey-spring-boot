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

package de.codecentric.spring.boot.chaos.monkey.endpoints;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.ChaosMonkeyDisabledDto;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.ChaosMonkeyEnabledDto;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** @author Benjamin Wilms */
class ChaosMonkeyRequestScopeJmxEndpointTest {

  private ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint;

  private ChaosMonkeySettings chaosMonkeySettings;

  @BeforeEach
  void setUp() {
    AssaultProperties assaultProperties = new AssaultProperties();
    assaultProperties.setLevel(1);
    assaultProperties.setLatencyRangeStart(100);
    assaultProperties.setLatencyRangeEnd(200);
    WatcherProperties watcherProperties = new WatcherProperties();
    watcherProperties.setComponent(true);
    ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
    chaosMonkeyProperties.setEnabled(true);
    chaosMonkeySettings =
        new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
    chaosMonkeyJmxEndpoint = new ChaosMonkeyJmxEndpoint(chaosMonkeySettings);
  }

  @Test
  void getAssaultProperties() {
    assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties())
        .isEqualTo(chaosMonkeySettings.getAssaultProperties().toDto());
  }

  @Test
  void toggleLatencyAssault() {
    boolean latencyActive = chaosMonkeySettings.getAssaultProperties().isLatencyActive();

    chaosMonkeyJmxEndpoint.toggleLatencyAssault();

    assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().getLatencyActive())
        .isNotEqualTo(latencyActive);
  }

  @Test
  void toggleExceptionAssault() {
    boolean exceptionsActive = chaosMonkeySettings.getAssaultProperties().isExceptionsActive();
    chaosMonkeyJmxEndpoint.toggleExceptionAssault();

    assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().getExceptionsActive())
        .isNotEqualTo(exceptionsActive);
  }

  @Test
  void toggleKillApplicationAssault() {
    boolean killApplicationActive =
        chaosMonkeySettings.getAssaultProperties().isKillApplicationActive();
    chaosMonkeyJmxEndpoint.toggleKillApplicationAssault();

    assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().getKillApplicationActive())
        .isNotEqualTo(killApplicationActive);
  }

  @Test
  void toggleCpuAssault() {
    boolean cpuActive = chaosMonkeySettings.getAssaultProperties().isCpuActive();
    chaosMonkeyJmxEndpoint.toggleCpuAssault();

    assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().getCpuActive())
        .isNotEqualTo(cpuActive);
  }

  @Test
  void isChaosMonkeyActive() {
    assertThat(chaosMonkeyJmxEndpoint.isChaosMonkeyActive())
        .isEqualTo(String.valueOf(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled()));
  }

  @Test
  void enableChaosMonkey() {
    ZonedDateTime enabledAt = ZonedDateTime.now();
    ChaosMonkeyEnabledDto enabledDto = chaosMonkeyJmxEndpoint.enableChaosMonkey();
    assertThat(enabledDto.getStatus()).isEqualTo("Chaos Monkey is enabled");
    assertThat(enabledDto.getEnabledAt()).isAfterOrEqualTo(enabledAt);
    assertThat(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled()).isTrue();
  }

  @Test
  void disableChaosMonkey() {
    ZonedDateTime disabledAt = ZonedDateTime.now();
    ChaosMonkeyDisabledDto disabledDto = chaosMonkeyJmxEndpoint.disableChaosMonkey();
    assertThat(disabledDto.getStatus()).isEqualTo("Chaos Monkey is disabled");
    assertThat(disabledDto.getDisabledAt()).isAfterOrEqualTo(disabledAt);
    assertThat(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled()).isFalse();
  }

  @Test
  void getWatcherProperties() {
    assertThat(chaosMonkeyJmxEndpoint.getWatcherProperties())
        .isEqualTo(chaosMonkeySettings.getWatcherProperties());
  }
}
