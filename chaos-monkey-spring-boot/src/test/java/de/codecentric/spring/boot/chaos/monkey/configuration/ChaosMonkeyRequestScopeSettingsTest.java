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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** @author Benjamin Wilms */
class ChaosMonkeyRequestScopeSettingsTest {

    @Test
    void noArgsTest() {
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        ChaosMonkeySettings settings = new ChaosMonkeySettings();
        settings.setChaosMonkeyProperties(chaosMonkeyProperties);
        settings.setAssaultProperties(assaultProperties);
        settings.setWatcherProperties(watcherProperties);

        validate(settings, chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Test
    void allArgsTest() {
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        ChaosMonkeySettings settings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);

        validate(settings, chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Test
    void lombokDataNullTest() {
        ChaosMonkeySettings settings = new ChaosMonkeySettings(null, null, null);
        assertNull(settings.getChaosMonkeyProperties());
        assertNull(settings.getAssaultProperties());
        assertNull(settings.getWatcherProperties());
    }

    private void validate(ChaosMonkeySettings settings, ChaosMonkeyProperties chaosMonkeyProperties, AssaultProperties assaultProperties, WatcherProperties watcherProperties) {
        assertEquals(settings.getChaosMonkeyProperties(), chaosMonkeyProperties);
        assertTrue(settings.getChaosMonkeyProperties().isEnabled());
        assertEquals(settings.getAssaultProperties(), assaultProperties);
        assertTrue(settings.getAssaultProperties().isKillApplicationActive());
        assertTrue(settings.getAssaultProperties().isExceptionsActive());
        assertTrue(settings.getAssaultProperties().isLatencyActive());
        assertEquals(settings.getWatcherProperties(), watcherProperties);
        assertTrue(settings.getWatcherProperties().isController());
        assertTrue(settings.getWatcherProperties().isRepository());
        assertTrue(settings.getWatcherProperties().isService());
        assertTrue(settings.getWatcherProperties().isRestController());

        int troubleRandom = settings.getAssaultProperties().getTroubleRandom();
        assertTrue(troubleRandom < 1001, "Trouble random is to high!");
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
