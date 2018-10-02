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
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.demo.chaos.monkey.controller.DemoController;
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
public class SpringControllerAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Mock
    private MetricEventPublisher metricsMock;

    private String pointcutName = "execution.DemoController.sayHello";
    private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.controller.DemoController.sayHello";


    @Test
    public void chaosMonkeyIsCalled() {
        DemoController target = new DemoController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, metricsMock);
        factory.addAspect(controllerAspect);

        DemoController proxy = factory.getProxy();
        proxy.sayHello();


        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock, times(1)).publishMetricEvent(MetricType.CONTROLLER, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }

    @Test
    public void chaosMonkeyIsCalled_Metrics_NULL() {
        DemoController target = new DemoController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, null);
        factory.addAspect(controllerAspect);

        DemoController proxy = factory.getProxy();
        proxy.sayHello();


        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock, times(0)).publishMetricEvent(MetricType.CONTROLLER, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoController target = new DemoController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock, null);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, null);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock, null);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(restControllerAspect);

        DemoController proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey(simpleName);
        verify(metricsMock, times(0)).publishMetricEvent(MetricType.CONTROLLER, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }
}