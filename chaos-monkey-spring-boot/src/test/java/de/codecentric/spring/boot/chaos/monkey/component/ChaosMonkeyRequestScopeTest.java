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

package de.codecentric.spring.boot.chaos.monkey.component;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggles;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Benjamin Wilms */
@ExtendWith(MockitoExtension.class)
class ChaosMonkeyRequestScopeTest {

  ChaosMonkeyRequestScope chaosMonkeyRequestScope;

  @Mock AssaultProperties assaultProperties;

  @Mock ChaosMonkeyProperties chaosMonkeyProperties;

  @Mock ChaosMonkeySettings chaosMonkeySettings;

  @Mock LatencyAssault latencyAssault;

  @Mock ExceptionAssault exceptionAssault;

  @Mock MetricEventPublisher metricEventPublisherMock;

  @BeforeEach
  void setUpCommon() {
    given(chaosMonkeySettings.getChaosMonkeyProperties()).willReturn(chaosMonkeyProperties);

    chaosMonkeyRequestScope =
        new ChaosMonkeyRequestScope(
            chaosMonkeySettings,
            Arrays.asList(latencyAssault, exceptionAssault),
            Collections.emptyList(),
            metricEventPublisherMock,
            new DefaultChaosToggles(),
            new DefaultChaosToggleNameMapper(chaosMonkeyProperties.getTogglePrefix()));
  }

  @Test
  void givenChaosMonkeyExecutionIsDisabledExpectNoInteractions() {
    given(chaosMonkeyProperties.isEnabled()).willReturn(false);

    chaosMonkeyRequestScope.callChaosMonkey(null, null);

    verify(latencyAssault, never()).attack();
    verify(exceptionAssault, never()).attack();
  }

  @Nested
  class GivenChaosMonekyExecutionIsEnabled {

    @BeforeEach
    void setUpForChaosMonkeyExecutionEnabled() {
      given(assaultProperties.getLevel()).willReturn(1);
      given(assaultProperties.getTroubleRandom()).willReturn(1);
      given(chaosMonkeyProperties.isEnabled()).willReturn(true);
      given(chaosMonkeySettings.getAssaultProperties()).willReturn(assaultProperties);
    }

    @Test
    void allAssaultsActiveExpectLatencyAttack() {
      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(0);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, times(1)).attack();
    }

    @Test
    void allAssaultsActiveExpectExceptionAttack() {
      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(1);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(exceptionAssault, times(1)).attack();
    }

    @Test
    void isLatencyAssaultActive() {
      given(latencyAssault.isActive()).willReturn(true);
      given(exceptionAssault.isActive()).willReturn(false);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, times(1)).attack();
    }

    @Test
    void isExceptionAssaultActive() {
      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(false);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(exceptionAssault, times(1)).attack();
    }

    @Test
    void isExceptionAndLatencyAssaultActiveExpectExceptionAttack() {
      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(1);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(exceptionAssault, times(1)).attack();
    }

    @Test
    void isExceptionAndLatencyAssaultActiveExpectLatencyAttack() {

      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(0);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, times(1)).attack();
    }

    @Test
    void isExceptionActiveExpectExceptionAttack() {
      given(exceptionAssault.isActive()).willReturn(true);
      given(latencyAssault.isActive()).willReturn(false);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(exceptionAssault, times(1)).attack();
    }

    @Test
    void isLatencyActiveExpectLatencyAttack() {
      given(exceptionAssault.isActive()).willReturn(false);
      given(latencyAssault.isActive()).willReturn(true);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, times(1)).attack();
    }

    @Test
    void givenNoAssaultsActiveExpectNoAttack() {
      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, never()).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void givenAssaultLevelTooHighExpectNoLogging() {
      given(assaultProperties.getLevel()).willReturn(1000);
      given(assaultProperties.getTroubleRandom()).willReturn(9);

      chaosMonkeyRequestScope.callChaosMonkey(null, null);

      verify(latencyAssault, never()).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void chaosMonkeyIsNotCalledWhenServiceNotWatched() {
      String customService = "CustomService";

      given(assaultProperties.getWatchedCustomServices())
          .willReturn(Collections.singletonList(customService));
      given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive())
          .willReturn(true);

      chaosMonkeyRequestScope.callChaosMonkey(null, "notInListService");

      verify(latencyAssault, never()).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void chaosMonkeyIsCalledWhenServiceIsWatched() {
      String customService = "CustomService";

      given(exceptionAssault.isActive()).willReturn(true);
      given(assaultProperties.getWatchedCustomServices())
          .willReturn(Collections.singletonList(customService));
      given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive())
          .willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(0);

      chaosMonkeyRequestScope.callChaosMonkey(null, customService);

      verify(latencyAssault, times(1)).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void chaosMonkeyIsCalledWhenServiceIsWatchedWhenSimpleNameIsMethodReference() {
      String customRepository = "org.springframework.data.repository.CrudRepository";

      given(exceptionAssault.isActive()).willReturn(true);
      given(assaultProperties.getWatchedCustomServices())
          .willReturn(Collections.singletonList(customRepository));
      given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive())
          .willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(0);

      String simpleName = customRepository + ".findAll";
      chaosMonkeyRequestScope.callChaosMonkey(null, simpleName);

      verify(latencyAssault, times(1)).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void chaosMonkeyIsCalledWhenServiceIsWatchedWhenSimpleNameIsPackageReference() {
      String packageName = "org.springframework.data.repository";

      given(exceptionAssault.isActive()).willReturn(true);
      given(assaultProperties.getWatchedCustomServices())
          .willReturn(Collections.singletonList(packageName));
      given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive())
          .willReturn(true);
      given(latencyAssault.isActive()).willReturn(true);
      given(assaultProperties.chooseAssault(2)).willReturn(0);

      String simpleName = packageName + "CrudRepository.findAll";
      chaosMonkeyRequestScope.callChaosMonkey(null, simpleName);

      verify(latencyAssault, times(1)).attack();
      verify(exceptionAssault, never()).attack();
    }

    @Test
    void shouldMakeUncategorizedCustomAssaultsRequestScopeByDefault() {
      // create an assault that is neither runtime nor request
      ChaosMonkeyAssault customAssault = mock(ChaosMonkeyAssault.class);
      given(customAssault.isActive()).willReturn(true);
      ChaosMonkeyRequestScope customScope =
          new ChaosMonkeyRequestScope(
              chaosMonkeySettings,
              Collections.emptyList(),
              Collections.singletonList(customAssault),
              metricEventPublisherMock,
              new DefaultChaosToggles(),
              new DefaultChaosToggleNameMapper(chaosMonkeyProperties.getTogglePrefix()));

      customScope.callChaosMonkey(null, "foo");
      verify(customAssault).attack();
    }
  }

  @Test
  void assaultShouldBeDeterministicIfConfigured() {
    ChaosMonkeyAssault customAssault = mock(ChaosMonkeyAssault.class);
    given(customAssault.isActive()).willReturn(true);
    given(chaosMonkeyProperties.isEnabled()).willReturn(true);
    given(chaosMonkeySettings.getAssaultProperties()).willReturn(assaultProperties);

    given(assaultProperties.isDeterministic()).willReturn(true);
    given(assaultProperties.getLevel()).willReturn(3);

    ChaosMonkeyRequestScope customScope =
        new ChaosMonkeyRequestScope(
            chaosMonkeySettings,
            Collections.emptyList(),
            Collections.singletonList(customAssault),
            metricEventPublisherMock,
            new DefaultChaosToggles(),
            new DefaultChaosToggleNameMapper(chaosMonkeyProperties.getTogglePrefix()));

    customScope.callChaosMonkey(null, "foo");
    verify(customAssault, never()).attack();
    customScope.callChaosMonkey(null, "foo");
    verify(customAssault, never()).attack();
    customScope.callChaosMonkey(null, "foo");
    verify(customAssault, times(1)).attack();
  }
}
