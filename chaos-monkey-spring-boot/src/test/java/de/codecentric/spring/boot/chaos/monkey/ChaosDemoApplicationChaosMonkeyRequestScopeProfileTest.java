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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggles;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** @author Benjamin Wilms */
@SpringBootTest(
    classes = ChaosDemoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "chaos.monkey.watcher.controller=true",
      "chaos.monkey.assaults.level=1",
      "chaos.monkey.assaults.latencyRangeStart=10",
      "chaos.monkey.assaults.latencyRangeEnd=50",
      "chaos.monkey.assaults.killApplicationActive=true",
      "spring.profiles.active=chaos-monkey"
    })
class ChaosDemoApplicationChaosMonkeyRequestScopeProfileTest {

  @Autowired private ChaosMonkeyRequestScope chaosMonkeyRequestScope;

  @Autowired private ChaosMonkeySettings monkeySettings;

  @Autowired private LatencyAssault latencyAssault;

  @Autowired private ExceptionAssault exceptionAssault;

  @Autowired private KillAppAssault killAppAssault;

  @Mock private MetricEventPublisher metricsMock;

  @BeforeEach
  void setUp() {
    chaosMonkeyRequestScope =
        new ChaosMonkeyRequestScope(
            monkeySettings,
            Arrays.asList(latencyAssault, exceptionAssault),
            Collections.emptyList(),
            metricsMock,
            new DefaultChaosToggles(),
            new DefaultChaosToggleNameMapper(
                monkeySettings.getChaosMonkeyProperties().getTogglePrefix()));
  }

  @Test
  void contextLoads() {
    assertNotNull(chaosMonkeyRequestScope);
  }

  @Test
  void checkChaosSettingsObject() {
    assertNotNull(monkeySettings);
  }

  @Test
  void checkChaosSettingsValues() {
    assertThat(monkeySettings.getChaosMonkeyProperties().isEnabled()).isFalse();
    assertThat(monkeySettings.getAssaultProperties().getLatencyRangeEnd()).isEqualTo(50);
    assertThat(monkeySettings.getAssaultProperties().getLatencyRangeStart()).isEqualTo(10);
    assertThat(monkeySettings.getAssaultProperties().getLevel()).isEqualTo(1);
    assertThat(monkeySettings.getAssaultProperties().isLatencyActive()).isFalse();
    assertThat(monkeySettings.getAssaultProperties().isExceptionsActive()).isFalse();
    assertThat(monkeySettings.getAssaultProperties().isKillApplicationActive()).isTrue();
    assertThat(monkeySettings.getAssaultProperties().getWatchedCustomServices()).isNull();
    assertThat(monkeySettings.getWatcherProperties().isController()).isTrue();
    assertThat(monkeySettings.getWatcherProperties().isRepository()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isRestController()).isFalse();
    assertThat(monkeySettings.getWatcherProperties().isService()).isFalse();
  }
}
