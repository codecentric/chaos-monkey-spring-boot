/*
 * Copyright 2018-2025 the original author or authors.
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

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRequestAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Benjamin Wilms, Dennis Effing
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChaosMonkeyRequestScopeTest {

    ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    @Spy
    AssaultProperties assaultProperties;

    @Mock
    ChaosMonkeyProperties chaosMonkeyProperties;

    @Mock
    ChaosMonkeySettings chaosMonkeySettings;

    @Mock
    LatencyAssault latencyAssault;

    @Mock
    ExceptionAssault exceptionAssault;

    @Mock
    MetricEventPublisher metricEventPublisherMock;

    private List<ChaosMonkeyRequestAssault> assaults;

    @BeforeEach
    void setUpCommon() {
        given(chaosMonkeySettings.getChaosMonkeyProperties()).willReturn(chaosMonkeyProperties);
        given(chaosMonkeySettings.getAssaultProperties()).willReturn(assaultProperties);

        // NB: The order of assaults in the array is important when attacks are chosen
        // by random
        // or when mocking the random result.
        assaults = Arrays.asList(latencyAssault, exceptionAssault);
        chaosMonkeyRequestScope = new ChaosMonkeyRequestScope(chaosMonkeySettings, assaults, Collections.emptyList(), metricEventPublisherMock,
                new DefaultChaosToggles(), new DefaultChaosToggleNameMapper(chaosMonkeyProperties.getTogglePrefix()));
    }

    @Test
    void callChaosMonkey_shouldNotRunActiveAttacksIfDisabled() {
        givenChaosMonkeyIsDisabled();
        givenMultipleActiveAttacks();

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(latencyAssault, never()).attack();
        verify(exceptionAssault, never()).attack();
    }

    @Test
    void callChaosMonkey_shouldNotPublishRequestCountMetricIfDisabled() {
        givenChaosMonkeyIsDisabled();

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenOneActiveAttack_shouldRunAttackIfEnabled() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0)).attack();
    }

    @Test
    void callChaosMonkey_givenMultipleActiveAttacks_shouldChooseAttackByRandom() {
        givenChaosMonkeyIsEnabled();
        givenMultipleActiveAttacks();
        given(assaultProperties.chooseAssault(assaults.size())).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0)).attack();
    }

    @Test
    void callChaosMonkey_givenRandomTrouble_shouldRunAttackAndPublishRequestCountMetricIfRandomTroubleIsHigherThanLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(false);
        given(assaultProperties.getTroubleRandom()).willReturn(3);
        given(assaultProperties.getLevel()).willReturn(2);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0)).attack();
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenRandomTrouble_shouldRunAttackAndPublishRequestCountMetricIfRandomTroubleIsEqualToLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(false);
        given(assaultProperties.getTroubleRandom()).willReturn(2);
        given(assaultProperties.getLevel()).willReturn(2);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0)).attack();
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenRandomTrouble_shouldNotRunAttackAndNotPublishRequestCountMetricIfRandomTroubleIsLowerThanLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(false);
        given(assaultProperties.getTroubleRandom()).willReturn(1);
        given(assaultProperties.getLevel()).willReturn(2);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0), never()).attack();
        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenDeterministicTrouble_shouldNotRunAndNotPublishRequestCountMetricAttackIfAttackCountIsLowerThanLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(true);
        given(assaultProperties.getLevel()).willReturn(2);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0), never()).attack();
        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock, never()).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenDeterministicTrouble_shouldRunAttackAndPublishRequestCountMetricIfAttackCountIsEqualToLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(true);
        given(assaultProperties.getLevel()).willReturn(2);

        chaosMonkeyRequestScope.callChaosMonkey(null, null);
        chaosMonkeyRequestScope.callChaosMonkey(null, null);

        verify(assaults.get(0)).attack();
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
        verify(metricEventPublisherMock).publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    @Test
    void callChaosMonkey_givenDeterministicTroubleAndConfiguredWatchedCustomServices_shouldNotRunAttackIfAttackCountIsLowerThanLevel() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isDeterministic()).willReturn(true);
        given(assaultProperties.getLevel()).willReturn(3);
        given(assaultProperties.isWatchedCustomServicesActive()).willReturn(true);
        given(assaultProperties.getWatchedCustomServices()).willReturn(List.of("de.test.CustomService.someMethod"));

        // Important: We call chaos monkey three times, but the second attack is not on
        // the watched custom service!
        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someMethod");
        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someOtherMethod");
        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someMethod");

        verify(assaults.get(0), never()).attack();
    }

    @Test
    void callChaosMonkey_givenConfiguredWatchedCustomServices_shouldRunAttackIfTargetPackageNameMatches() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isWatchedCustomServicesActive()).willReturn(true);
        given(assaultProperties.getWatchedCustomServices()).willReturn(List.of("de.test"));

        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someMethod");

        verify(assaults.get(0)).attack();
    }

    @Test
    void callChaosMonkey_givenConfiguredWatchedCustomServices_shouldRunAttackIfTargetClassNameMatches() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isWatchedCustomServicesActive()).willReturn(true);
        given(assaultProperties.getWatchedCustomServices()).willReturn(List.of("de.test.CustomService"));

        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someMethod");

        verify(assaults.get(0)).attack();
    }

    @Test
    void callChaosMonkey_givenConfiguredWatchedCustomServices_shouldRunAttackIfTargetMethodNameMatches() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isWatchedCustomServicesActive()).willReturn(true);
        given(assaultProperties.getWatchedCustomServices()).willReturn(List.of("de.test.CustomService.someMethod"));

        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someMethod");

        verify(assaults.get(0)).attack();
    }

    @Test
    void callChaosMonkey_givenConfiguredCustomServices_shouldNotRunAttackIfTargetSimpleNameDoesNotMatch() {
        givenChaosMonkeyIsEnabled();
        givenOneActiveAttack();
        given(assaultProperties.isWatchedCustomServicesActive()).willReturn(true);
        given(assaultProperties.getWatchedCustomServices()).willReturn(List.of("de.test.CustomService.someMethod"));

        chaosMonkeyRequestScope.callChaosMonkey(null, "de.test.CustomService.someOtherMethod");

        verify(assaults.get(0), never()).attack();
    }

    private void givenChaosMonkeyIsEnabled() {
        given(chaosMonkeyProperties.isEnabled()).willReturn(true);
    }

    private void givenChaosMonkeyIsDisabled() {
        given(chaosMonkeyProperties.isEnabled()).willReturn(false);
    }

    private void givenOneActiveAttack() {
        given(latencyAssault.isActive()).willReturn(true);
        given(exceptionAssault.isActive()).willReturn(false);
    }

    private void givenMultipleActiveAttacks() {
        given(exceptionAssault.isActive()).willReturn(true);
        given(latencyAssault.isActive()).willReturn(true);
    }
}
