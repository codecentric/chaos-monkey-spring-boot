/*
 * Copyright 2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import static org.mockito.Mockito.*;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.bean.DemoBean;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChaosMonkeyBeanPostProcessorTest {

    private final DemoBean target = new DemoBean();

    private final WatcherProperties watcherProperties = new WatcherProperties();

    @Mock
    private ChaosMonkeyRequestScope requestScope;

    @Mock
    private MetricEventPublisher metrics;

    private ChaosMonkeyBeanPostProcessor postProcessor;

    private final String pointcutName = "execution.DemoBean.sayHello";

    private final String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.bean.DemoBean.sayHello";

    @BeforeEach
    void setup() {
        postProcessor = new ChaosMonkeyBeanPostProcessor(watcherProperties, requestScope, metrics);
    }

    @Test
    void chaosMonkeyIsCalledWhenEnabledInConfig() {
        watcherProperties.setBeans(Collections.singletonList("demoBean"));

        callTargetMethod();

        verifyDependenciesCalledXTimes(1);
    }

    @Test
    void chaosMonkeyIsNotCalledWhenDisabledInConfig() {
        watcherProperties.setBeans(Collections.emptyList());

        callTargetMethod();

        verifyDependenciesCalledXTimes(0);
    }

    @Test
    void chaosMonkeyIsNotCalledWithUnrelatedBeansInConfig() {
        watcherProperties.setBeans(Collections.singletonList("demoComponent"));

        callTargetMethod();

        verifyDependenciesCalledXTimes(0);
    }

    private void callTargetMethod() {
        DemoBean proxy = (DemoBean) postProcessor.postProcessAfterInitialization(target, "demoBean");
        proxy.sayHello();
    }

    private void verifyDependenciesCalledXTimes(int i) {
        verify(requestScope, times(i)).callChaosMonkey(ChaosTarget.BEAN, simpleName);
        verify(metrics, times(i)).publishMetricEvent(pointcutName, MetricType.BEAN);
        verifyNoMoreInteractions(requestScope, metrics);
    }
}
