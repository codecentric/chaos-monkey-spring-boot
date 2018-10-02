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
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService;
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
public class SpringServiceAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Mock
    private MetricEventPublisher metricsMock;

    private String pointcutName = "execution.DemoService.sayHelloService";
    private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService.sayHelloService";



    @Test
    public void chaosMonkeyIsCalled() {
        DemoService serviceTarget = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(serviceTarget);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, metricsMock);
        factory.addAspect(serviceAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock, times(1)).publishMetricEvent(MetricType.SERVICE, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }

    @Test
    public void chaosMonkeyIsCalled_Metrics_NULL() {
        DemoService serviceTarget = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(serviceTarget);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, null);
        factory.addAspect(serviceAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock, times(0)).publishMetricEvent(MetricType.SERVICE, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoService target = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock, metricsMock);

        factory.addAspect(controllerAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey(simpleName);
        verify(metricsMock, times(0)).publishMetricEvent(MetricType.SERVICE, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);

    }

    @Test
    public void chaosMonkeyIsCalledWhenServiceWatched() {
        DemoService target = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock, metricsMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, metricsMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();


        verify(chaosMonkeyMock, times(1)).callChaosMonkey(simpleName);
        verify(metricsMock, times(1)).publishMetricEvent(MetricType.SERVICE, pointcutName);
        verifyNoMoreInteractions(chaosMonkeyMock, metricsMock);
    }


}