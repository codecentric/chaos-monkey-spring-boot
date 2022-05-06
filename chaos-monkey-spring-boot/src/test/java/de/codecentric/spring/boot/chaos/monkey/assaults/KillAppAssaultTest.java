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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

/** @author Thorsten Deelmann */
@ExtendWith(MockitoExtension.class)
class KillAppAssaultTest {

    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Mock
    private MetricEventPublisher metricsMock;

    @BeforeEach
    void setUp() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.addAppender(mockAppender);

        captorLoggingEvent = ArgumentCaptor.forClass(LoggingEvent.class);
    }

    @Test
    void killsSpringBootApplication() {
        KillAppAssault killAppAssault = new KillAppAssault(null, metricsMock);
        killAppAssault.attack();

        verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());

        assertEquals(Level.INFO, captorLoggingEvent.getAllValues().get(0).getLevel());
        assertEquals(Level.INFO, captorLoggingEvent.getAllValues().get(1).getLevel());
        assertEquals("Chaos Monkey - I am killing your Application!", captorLoggingEvent.getAllValues().get(0).getMessage());
        assertEquals("Chaos Monkey - Unable to kill the App, I am not the BOSS!", captorLoggingEvent.getAllValues().get(1).getMessage());
    }
}
