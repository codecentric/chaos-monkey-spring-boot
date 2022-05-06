/*
 * Copyright 2019-2022 the original author or authors.
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.AbstractIntegerAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class LatencyAssaultRangeTest {

    @Captor
    private ArgumentCaptor<AtomicInteger> captorTimeoutValue;

    @Test
    void fixedLatencyIsPossible() {
        final int fixedLatency = 1000;

        assertThatLatencyConfiguration(fixedLatency, fixedLatency).isEqualTo(fixedLatency);
    }

    @Test
    void latencyRangeIsPossible() {
        final int latencyRangeStart = 1000;
        final int latencyRangeEnd = 5000;

        assertThatLatencyConfiguration(latencyRangeStart, latencyRangeEnd).isBetween(latencyRangeStart, latencyRangeEnd);
    }

    private AbstractIntegerAssert<?> assertThatLatencyConfiguration(int latencyRangeStart, int latencyRangeEnd) {
        final AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLatencyRangeStart(latencyRangeStart);
        assaultProperties.setLatencyRangeEnd(latencyRangeEnd);

        final ChaosMonkeySettings chaosMonkeySettings = mock(ChaosMonkeySettings.class);
        when(chaosMonkeySettings.getAssaultProperties()).thenReturn(assaultProperties);

        final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        doNothing().when(publisher).publishEvent(any(ApplicationEvent.class));

        final MetricEventPublisher metricEventPublisher = spy(new MetricEventPublisher());
        metricEventPublisher.setApplicationEventPublisher(publisher);

        final LatencyAssault latencyAssault = new LatencyAssault(chaosMonkeySettings, metricEventPublisher);
        latencyAssault.attack();

        verify(metricEventPublisher).publishMetricEvent(eq(MetricType.LATENCY_ASSAULT), captorTimeoutValue.capture());
        return assertThat(captorTimeoutValue.getValue().get());
    }
}
