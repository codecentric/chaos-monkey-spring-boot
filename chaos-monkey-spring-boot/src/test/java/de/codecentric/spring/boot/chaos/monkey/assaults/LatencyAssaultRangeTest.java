/*
 * Copyright 2019-2025 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;

import de.codecentric.spring.boot.chaos.monkey.events.MetricEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class LatencyAssaultRangeTest {
    ChaosMonkeySettings chaosMonkeySettings;

    @Captor
    ArgumentCaptor<MetricEvent> eventArgumentCaptor;

    @InjectMocks
    MetricEventPublisher metricEventPublisher;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        this.chaosMonkeySettings = new ChaosMonkeySettings();
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLatencyRangeStart(1000);
        chaosMonkeySettings.setAssaultProperties(assaultProperties);
    }

    @Test
    void fixedLatencyIsPossible() {
        chaosMonkeySettings.getAssaultProperties().setLatencyRangeEnd(1000);

        LatencyAssault latencyAssault = new LatencyAssault(chaosMonkeySettings, metricEventPublisher);
        latencyAssault.attack();

        verify(applicationEventPublisher, times(2)).publishEvent(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue().getMetricType()).isEqualTo(MetricType.LATENCY_ASSAULT);
        assertThat(eventArgumentCaptor.getValue().getMetricValue()).isEqualTo(1000);
    }

    @Test
    void latencyRangeIsPossible() {
        chaosMonkeySettings.getAssaultProperties().setLatencyRangeEnd(5000);

        LatencyAssault latencyAssault = new LatencyAssault(chaosMonkeySettings, metricEventPublisher);
        latencyAssault.attack();

        verify(applicationEventPublisher, times(2)).publishEvent(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue().getMetricType()).isEqualTo(MetricType.LATENCY_ASSAULT);
        assertThat(eventArgumentCaptor.getValue().getMetricValue()).isBetween(1000.0, 5000.0);
    }
}
