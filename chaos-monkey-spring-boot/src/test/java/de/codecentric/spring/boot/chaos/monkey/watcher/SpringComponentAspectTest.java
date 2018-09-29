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

package de.codecentric.spring.boot.chaos.monkey.watcher;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import io.micrometer.core.instrument.Counter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.mockito.Mockito.*;


/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringComponentAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Mock
    private Metrics metricsMock;
    @Mock
    private Counter counterMock;

    private String pointcutName = "execution.DemoComponent.sayHello";
    private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello";

    @Before
    public void before() {
        when(metricsMock.counterWatcher(MetricType.COMPONENT, pointcutName)).thenReturn(counterMock);
    }


    @Test
    public void chaosMonkeyIsCalled() {
        DemoComponent target = new DemoComponent();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringComponentAspect componentAspect = new SpringComponentAspect(chaosMonkeyMock, metricsMock);
        factory.addAspect(componentAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();



        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock,times(1)).counterWatcher(MetricType.COMPONENT, pointcutName);
        verify(counterMock,times(1)).increment();
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock, counterMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoComponent target = new DemoComponent();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock, metricsMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, metricsMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock, metricsMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(restControllerAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey(simpleName);
        verify(metricsMock,times(0)).counterWatcher(MetricType.COMPONENT, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock, counterMock);

    }

}