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

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkeyJmxEndpointTest {

    private ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint;
    private ChaosMonkeySettings chaosMonkeySettings;

    @Before
    public void setUp() throws Exception {

        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLevel(1);
        assaultProperties.setLatencyRangeStart(100);
        assaultProperties.setLatencyRangeEnd(200);
        WatcherProperties watcherProperties = new WatcherProperties();
        watcherProperties.setComponent(true);
        ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
        chaosMonkeyProperties.setEnabled(true);
        chaosMonkeySettings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
        chaosMonkeyJmxEndpoint = new ChaosMonkeyJmxEndpoint(chaosMonkeySettings);
    }

    @Test
    public void getAssaultProperties() {

        assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties(), is(chaosMonkeySettings.getAssaultProperties()));
    }

    @Test
    public void toggleLatencyAssault() {
        boolean latencyActive = chaosMonkeySettings.getAssaultProperties().isLatencyActive();

        chaosMonkeyJmxEndpoint.toggleLatencyAssault();

        assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().isLatencyActive(), not(latencyActive));
    }

    @Test
    public void toggleExceptionAssault() {
        boolean exceptionsActive = chaosMonkeySettings.getAssaultProperties().isExceptionsActive();
        chaosMonkeyJmxEndpoint.toggleExceptionAssault();

        assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().isExceptionsActive(), not(exceptionsActive));
    }

    @Test
    public void toggleKillApplicationAssault() {
        boolean killApplicationActive = chaosMonkeySettings.getAssaultProperties().isKillApplicationActive();
        chaosMonkeyJmxEndpoint.toggleKillApplicationAssault();

        assertThat(chaosMonkeyJmxEndpoint.getAssaultProperties().isKillApplicationActive(), not(killApplicationActive));
    }

    @Test
    public void isChaosMonkeyActive() {
        assertThat(chaosMonkeyJmxEndpoint.isChaosMonkeyActive(), is(String.valueOf(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled())));
    }

    @Test
    public void enableChaosMonkey() {
        assertThat(chaosMonkeyJmxEndpoint.enableChaosMonkey(), is("Chaos Monkey is enabled"));
        assertThat(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled(), is(true));
    }

    @Test
    public void disableChaosMonkey() {
        assertThat(chaosMonkeyJmxEndpoint.disableChaosMonkey(), is("Chaos Monkey is disabled"));
        assertThat(chaosMonkeySettings.getChaosMonkeyProperties().isEnabled(), is(false));
    }

    @Test
    public void getWatcherProperties() {
        assertThat(chaosMonkeyJmxEndpoint.getWatcherProperties(), is(chaosMonkeySettings.getWatcherProperties()));
    }
}