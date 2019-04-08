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

package de.codecentric.spring.boot.chaos.monkey;

import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"chaos.monkey" +
        ".watcher.controller=true", "chaos.monkey.assaults.level=1", "chaos.monkey.assaults.latencyRangeStart=10", "chaos.monkey.assaults" +
        ".latencyRangeEnd=50", "chaos.monkey.assaults" +
        ".killApplicationActive=true", "spring.profiles" +
        ".active=chaos-monkey"})
public class ChaosDemoApplicationChaosMonkeyRequestScopeProfileTest {

    @Autowired
    private ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    @Autowired
    private ChaosMonkeySettings monkeySettings;

    @Autowired
    private LatencyAssault latencyAssault;

    @Autowired
    private ExceptionAssault exceptionAssault;

    @Autowired
    private KillAppAssault killAppAssault;


    @Mock
    private MetricEventPublisher metricsMock;

    @Before
    public void setUp() {
        chaosMonkeyRequestScope = new ChaosMonkeyRequestScope(monkeySettings, Arrays.asList(latencyAssault, exceptionAssault), Collections.emptyList(), metricsMock);
    }

    @Test
    public void contextLoads() {
        assertNotNull(chaosMonkeyRequestScope);
    }


    @Test
    public void checkChaosSettingsObject() {
        assertNotNull(monkeySettings);
    }

    @Test
    public void checkChaosSettingsValues() {
        assertThat(monkeySettings.getChaosMonkeyProperties().isEnabled(), is(false));
        assertThat(monkeySettings.getAssaultProperties().getLatencyRangeEnd(), is(50));
        assertThat(monkeySettings.getAssaultProperties().getLatencyRangeStart(), is(10));
        assertThat(monkeySettings.getAssaultProperties().getLevel(), is(1));
        assertThat(monkeySettings.getAssaultProperties().isLatencyActive(), is(true));
        assertThat(monkeySettings.getAssaultProperties().isExceptionsActive(), is(false));
        assertThat(monkeySettings.getAssaultProperties().isKillApplicationActive(), is(true));
        assertThat(monkeySettings.getAssaultProperties().getWatchedCustomServices(), is(nullValue()));
        assertThat(monkeySettings.getWatcherProperties().isController(), is(true));
        assertThat(monkeySettings.getWatcherProperties().isRepository(), is(false));
        assertThat(monkeySettings.getWatcherProperties().isRestController(), is(false));
        assertThat(monkeySettings.getWatcherProperties().isService(), is(true));

    }
}