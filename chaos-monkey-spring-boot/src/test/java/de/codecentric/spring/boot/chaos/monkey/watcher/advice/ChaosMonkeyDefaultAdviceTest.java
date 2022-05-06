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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyCreatorSupport;

class ChaosMonkeyDefaultAdviceTest {

    private boolean isEnabled = false;
    private final ChaosMonkeyRequestScope requestScope = mock(ChaosMonkeyRequestScope.class);
    private final MetricEventPublisher eventPublisher = mock(MetricEventPublisher.class);
    private final Advice advice = new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.COMPONENT, () -> isEnabled);
    private DemoComponent proxy;

    @BeforeEach
    public void setup() {
        ProxyCreatorSupport proxyCreator = new ProxyCreatorSupport();
        proxyCreator.addAdvice(advice);
        proxyCreator.setTarget(new DemoComponent());
        proxy = (DemoComponent) proxyCreator.getAopProxyFactory().createAopProxy(proxyCreator).getProxy();
        reset(requestScope, eventPublisher);
    }

    @Test
    public void shouldCallChaosMonkeyIfEnabled() {
        isEnabled = true;

        proxy.sayHello();

        verify(requestScope, times(1)).callChaosMonkey(ChaosTarget.COMPONENT,
                "de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello");
        verify(eventPublisher, times(1)).publishMetricEvent("execution.DemoComponent.sayHello", MetricType.COMPONENT);
        verifyNoMoreInteractions(requestScope, eventPublisher);
    }

    @Test
    public void shouldNotCallChaosMonkeyIfDisabled() {
        isEnabled = false;

        proxy.sayHello();

        verifyNoInteractions(requestScope, eventPublisher);
    }
}
