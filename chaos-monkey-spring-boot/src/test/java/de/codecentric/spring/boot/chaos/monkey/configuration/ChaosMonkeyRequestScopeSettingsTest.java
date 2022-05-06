/*
 * Copyright 2018-2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** @author Benjamin Wilms */
class ChaosMonkeyRequestScopeSettingsTest {

    private ChaosMonkeySettings settings;

    @Test
    void noArgsTest() {
        settings = new ChaosMonkeySettings();
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        settings.setChaosMonkeyProperties(chaosMonkeyProperties);
        AssaultProperties assaultProperties = getAssaultProperties();
        settings.setAssaultProperties(assaultProperties);
        WatcherProperties watcherProperties = getWatcherProperties();
        settings.setWatcherProperties(watcherProperties);

        validate(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Test
    void allArgsTest() {
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);

        validate(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Test
    void lombokDataTest() {
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);

        assertThat(settings.getChaosMonkeyProperties()).isNotNull();
        assertThat(settings.getAssaultProperties()).isNotNull();
        assertThat(settings.getWatcherProperties()).isNotNull();
    }

    @Test
    void lombokDataSetTest() {
        settings = new ChaosMonkeySettings();

        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        settings.setChaosMonkeyProperties(chaosMonkeyProperties);

        WatcherProperties watcherProperties = getWatcherProperties();
        settings.setWatcherProperties(watcherProperties);

        AssaultProperties assaultProperties = getAssaultProperties();
        settings.setAssaultProperties(assaultProperties);

        assertThat(settings.getChaosMonkeyProperties()).isNotNull();
        assertThat(settings.getAssaultProperties()).isNotNull();
        assertThat(settings.getWatcherProperties()).isNotNull();
    }

    @Test
    void lombokDataNullTest() {
        settings = new ChaosMonkeySettings(null, null, null);

        assertThat(settings.getChaosMonkeyProperties()).isNull();
        assertThat(settings.getAssaultProperties()).isNull();
        assertThat(settings.getWatcherProperties()).isNull();
    }

    private void validate(ChaosMonkeyProperties chaosMonkeyProperties, AssaultProperties assaultProperties, WatcherProperties watcherProperties) {
        assertThat(settings.getChaosMonkeyProperties()).isEqualTo(chaosMonkeyProperties);
        assertThat(settings.getAssaultProperties()).isEqualTo(assaultProperties);
        assertThat(settings.getWatcherProperties()).isEqualTo(watcherProperties);
        assertThat(settings.getChaosMonkeyProperties().isEnabled()).isTrue();
        assertThat(settings.getWatcherProperties().isController()).isTrue();
        assertThat(settings.getWatcherProperties().isRepository()).isTrue();
        assertThat(settings.getWatcherProperties().isService()).isTrue();
        assertThat(settings.getWatcherProperties().isRestController()).isTrue();
        assertThat(settings.getAssaultProperties().isKillApplicationActive()).isTrue();
        assertThat(settings.getAssaultProperties().isExceptionsActive()).isTrue();
        assertThat(settings.getAssaultProperties().isLatencyActive()).isTrue();
        assertThat(settings.getAssaultProperties().getLevel()).isEqualTo(assaultProperties.getLevel());
        assertThat(settings.getAssaultProperties().getLatencyRangeEnd()).isEqualTo(assaultProperties.getLatencyRangeEnd());
        assertThat(settings.getAssaultProperties().getLatencyRangeStart()).isEqualTo(assaultProperties.getLatencyRangeStart());

        int troubleRandom = settings.getAssaultProperties().getTroubleRandom();
        assertTrue(troubleRandom < 1001, "Trouble random is to high!");

        assertThat(settings.getAssaultProperties().getLevel()).isEqualTo(assaultProperties.getLevel());
    }

    private ChaosMonkeyProperties getChaosMonkeyProperties() {
        ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
        chaosMonkeyProperties.setEnabled(true);
        return chaosMonkeyProperties;
    }

    private AssaultProperties getAssaultProperties() {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setExceptionsActive(true);
        assaultProperties.setKillApplicationActive(true);
        assaultProperties.setLatencyActive(true);
        assaultProperties.setLatencyRangeEnd(100);
        assaultProperties.setLatencyRangeStart(1);
        assaultProperties.setLevel(99);
        return assaultProperties;
    }

    private WatcherProperties getWatcherProperties() {
        WatcherProperties watcherProperties = new WatcherProperties();
        watcherProperties.setController(true);
        watcherProperties.setRepository(true);
        watcherProperties.setRestController(true);
        watcherProperties.setService(true);
        return watcherProperties;
    }
}
