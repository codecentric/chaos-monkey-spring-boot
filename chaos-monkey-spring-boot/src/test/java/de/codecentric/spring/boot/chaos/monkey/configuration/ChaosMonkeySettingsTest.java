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

package de.codecentric.spring.boot.chaos.monkey.configuration;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkeySettingsTest {

    private ChaosMonkeySettings settings;

    @Test
    public void noArgsTest() {

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
    public void allArgsTest() {
        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);

        validate(chaosMonkeyProperties, assaultProperties, watcherProperties);

    }

    @Test
    public void lombokDataTest() {

        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);

        assertThat(settings.getChaosMonkeyProperties(), notNullValue());
        assertThat(settings.getAssaultProperties(), notNullValue());
        assertThat(settings.getWatcherProperties(), notNullValue());

    }

    @Test
    public void lombokDataSetTest() {

        ChaosMonkeyProperties chaosMonkeyProperties = getChaosMonkeyProperties();
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = new ChaosMonkeySettings();
        settings.setChaosMonkeyProperties(chaosMonkeyProperties);
        settings.setAssaultProperties(assaultProperties);
        settings.setWatcherProperties(watcherProperties);

        assertThat(settings.getChaosMonkeyProperties(), notNullValue());
        assertThat(settings.getAssaultProperties(), notNullValue());
        assertThat(settings.getWatcherProperties(), notNullValue());

    }

    @Test
    public void lombokDataNullTest() {

        settings = new ChaosMonkeySettings(null,null, null);

        assertThat(settings.getChaosMonkeyProperties(), nullValue());
        assertThat(settings.getAssaultProperties(), nullValue());
        assertThat(settings.getWatcherProperties(), nullValue());

    }

    private void validate(ChaosMonkeyProperties chaosMonkeyProperties, AssaultProperties assaultProperties, WatcherProperties watcherProperties) {
        assertThat(settings.getChaosMonkeyProperties(), is(chaosMonkeyProperties));
        assertThat(settings.getAssaultProperties(), is(assaultProperties));
        assertThat(settings.getWatcherProperties(), is(watcherProperties));
        assertThat(settings.getChaosMonkeyProperties().isEnabled(), is(true));
        assertThat(settings.getWatcherProperties().isController(), is(true));
        assertThat(settings.getWatcherProperties().isRepository(), is(true));
        assertThat(settings.getWatcherProperties().isService(), is(true));
        assertThat(settings.getWatcherProperties().isRestController(), is(true));
        assertThat(settings.getAssaultProperties().isKillApplicationActive(), is(true));
        assertThat(settings.getAssaultProperties().isExceptionsActive(), is(true));
        assertThat(settings.getAssaultProperties().isLatencyActive(), is(true));
        assertThat(settings.getAssaultProperties().getLevel(), is(assaultProperties.getLevel()));
        assertThat(settings.getAssaultProperties().getLatencyRangeEnd(), is(assaultProperties.getLatencyRangeEnd()));
        assertThat(settings.getAssaultProperties().getLatencyRangeStart(), is(assaultProperties.getLatencyRangeStart()));

        int troubleRandom = settings.getAssaultProperties().getTroubleRandom();
        assertTrue("Trouble random is to high!", troubleRandom < 11);

        assertThat(settings.getAssaultProperties().getLevel(), is(assaultProperties.getLevel()));
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