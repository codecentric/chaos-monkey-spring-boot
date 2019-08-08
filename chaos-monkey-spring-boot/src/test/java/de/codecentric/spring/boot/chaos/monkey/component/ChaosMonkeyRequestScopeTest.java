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

import de.codecentric.spring.boot.chaos.monkey.assaults.*;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class ChaosMonkeyRequestScopeTest {

    private ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    @Mock
    private AssaultProperties assaultProperties;

    @Mock
    private ChaosMonkeyProperties chaosMonkeyProperties;

    @Mock
    private ChaosMonkeySettings chaosMonkeySettings;

    @Mock
    private LatencyAssault latencyAssault;

    @Mock
    private ExceptionAssault exceptionAssault;

    @Mock
    private KillAppAssault killAppAssault;

    @Mock
    private MemoryAssault memoryAssaultMock;

    @Mock
    private MetricEventPublisher metricEventPublisherMock;


    @Before
    public void setUp() {
        given(this.assaultProperties.getLevel()).willReturn(1);
        given(this.assaultProperties.getTroubleRandom()).willReturn(1);
        given(this.chaosMonkeyProperties.isEnabled()).willReturn(true);
        given(this.chaosMonkeySettings.getAssaultProperties()).willReturn(this.assaultProperties);
        given(this.chaosMonkeySettings.getChaosMonkeyProperties()).willReturn(this.chaosMonkeyProperties);

        chaosMonkeyRequestScope = new ChaosMonkeyRequestScope(chaosMonkeySettings, Arrays.asList(latencyAssault, exceptionAssault), Collections.emptyList(), metricEventPublisherMock);
    }

    @Test
    public void allAssaultsActiveExpectLatencyAttack() {
        given(this.exceptionAssault.isActive()).willReturn(true);
        given(this.latencyAssault.isActive()).willReturn(true);
        given(this.killAppAssault.isActive()).willReturn(true);
        given(this.assaultProperties.chooseAssault(2)).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, times(1)).attack();
    }

    @Test
    public void allAssaultsActiveExpectExceptionAttack() {
        given(this.exceptionAssault.isActive()).willReturn(true);
        given(this.latencyAssault.isActive()).willReturn(true);
        given(this.killAppAssault.isActive()).willReturn(true);
        given(this.assaultProperties.chooseAssault(2)).willReturn(1);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(exceptionAssault, times(1)).attack();
    }


    @Test
    public void isLatencyAssaultActive() {
        given(this.latencyAssault.isActive()).willReturn(true);
        given(this.exceptionAssault.isActive()).willReturn(false);
        given(this.killAppAssault.isActive()).willReturn(false);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, times(1)).attack();
    }

    @Test
    public void isExceptionAssaultActive() {
        given(this.exceptionAssault.isActive()).willReturn(true);
        given(this.latencyAssault.isActive()).willReturn(false);
        given(this.killAppAssault.isActive()).willReturn(false);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(exceptionAssault, times(1)).attack();
    }

    @Test
    public void isExceptionAndLatencyAssaultActiveExpectExceptionAttack() {
        given(this.exceptionAssault.isActive()).willReturn(true);
        given(this.latencyAssault.isActive()).willReturn(true);
        given(this.killAppAssault.isActive()).willReturn(false);
        given(this.assaultProperties.chooseAssault(2)).willReturn(1);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(exceptionAssault, times(1)).attack();
    }

    @Test
    public void isExceptionAndLatencyAssaultActiveExpectLatencyAttack() {

        given(this.exceptionAssault.isActive()).willReturn(true);
        given(this.latencyAssault.isActive()).willReturn(true);
        given(this.killAppAssault.isActive()).willReturn(false);
        given(this.assaultProperties.chooseAssault(2)).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, times(1)).attack();
    }

    @Test
    public void isExceptionAndKillAssaultActiveExpectExceptionAttack() {
        given(exceptionAssault.isActive()).willReturn(true);
        given(latencyAssault.isActive()).willReturn(false);
        given(this.killAppAssault.isActive()).willReturn(true);
        given(this.assaultProperties.chooseAssault(2)).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(exceptionAssault, times(1)).attack();
    }


    @Test
    public void isLatencyAndKillAssaultActiveExpectLatencyAttack() {
        given(exceptionAssault.isActive()).willReturn(false);
        given(latencyAssault.isActive()).willReturn(true);
        given(this.killAppAssault.isActive()).willReturn(true);
        given(this.assaultProperties.chooseAssault(2)).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, times(1)).attack();
    }

    @Test
    public void givenNoAssaultsActiveExpectNoAttack() {
        given(exceptionAssault.isActive()).willReturn(false);
        given(latencyAssault.isActive()).willReturn(false);
        given(killAppAssault.isActive()).willReturn(false);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, never()).attack();
        verify(exceptionAssault, never()).attack();
        verify(killAppAssault, never()).attack();
    }

    @Test
    public void givenAssaultLevelTooHighExpectNoLogging() {
        given(this.assaultProperties.getLevel()).willReturn(1000);
        given(this.assaultProperties.getTroubleRandom()).willReturn(9);
        given(this.latencyAssault.isActive()).willReturn(true);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, never()).attack();
        verify(exceptionAssault, never()).attack();
        verify(killAppAssault, never()).attack();
    }

    @Test
    public void isChaosMonkeyExecutionDisabled() {
        given(this.chaosMonkeyProperties.isEnabled()).willReturn(false);

        chaosMonkeyRequestScope.callChaosMonkey(null);

        verify(latencyAssault, never()).attack();
        verify(exceptionAssault, never()).attack();
        verify(killAppAssault, never()).attack();
    }

    @Test
    public void chaosMonkeyIsNotCalledWhenServiceNotWatched() {
        String customService = "CustomService";

        given(exceptionAssault.isActive()).willReturn(true);
        given(this.assaultProperties.getWatchedCustomServices()).willReturn(Arrays.asList(customService));
        given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive()).willReturn(true);

        chaosMonkeyRequestScope.callChaosMonkey("notInListService");

        verify(latencyAssault, never()).attack();
        verify(exceptionAssault, never()).attack();
        verify(killAppAssault, never()).attack();

    }

    @Test
    public void chaosMonkeyIsCalledWhenServiceNotWatched() {
        String customService = "CustomService";

        given(exceptionAssault.isActive()).willReturn(true);
        given(this.assaultProperties.getWatchedCustomServices()).willReturn(Arrays.asList(customService));
        given(chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive()).willReturn(true);
        given(latencyAssault.isActive()).willReturn(true);
        given(this.assaultProperties.chooseAssault(2)).willReturn(0);

        chaosMonkeyRequestScope.callChaosMonkey(customService);

        verify(latencyAssault, times(1)).attack();
        verify(exceptionAssault, never()).attack();
        verify(killAppAssault, never()).attack();
    }

    @Test
    public void shouldMakeUncategorizedCustomAssaultsRequestScopeByDefault() {
        // create an assault that is neither runtime nor request
        ChaosMonkeyAssault customAssault = mock(ChaosMonkeyAssault.class);
        given(customAssault.isActive()).willReturn(true);
        ChaosMonkeyRequestScope customScope = new ChaosMonkeyRequestScope(chaosMonkeySettings, Collections.emptyList(), Collections.singletonList(customAssault), metricEventPublisherMock);

        customScope.callChaosMonkey("foo");
        verify(customAssault).attack();
    }
}
