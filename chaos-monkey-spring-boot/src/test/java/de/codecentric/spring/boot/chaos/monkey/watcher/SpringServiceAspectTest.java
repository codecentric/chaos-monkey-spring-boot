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

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.mockito.Mockito.*;

/**
 * @author Benjamin Wilms
 */
@ExtendWith(MockitoExtension.class)
class SpringServiceAspectTest {

    private DemoService target = new DemoService();
    private WatcherProperties watcherProperties = new WatcherProperties();
    private AspectJProxyFactory factory = new AspectJProxyFactory(target);

    @Mock
    private ChaosMonkeyRequestScope chaosMonkeyRequestScopeMock;

    @Mock
    private MetricEventPublisher metricsMock;

    private String pointcutName = "execution.DemoService.sayHello";
    private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService.sayHello";


    @Test
    void chaosMonkeyIsCalledWhenEnabledInConfig() {
        watcherProperties.setService(true);

        addRelevantAspect();

        callTargetMethod();

        verifyDependenciesCalledXTimes(1);
    }

    @Test
    void chaosMonkeyIsNotCalledWhenDisabledInConfig() {
        watcherProperties.setService(false);

        addRelevantAspect();

        callTargetMethod();

        verifyDependenciesCalledXTimes(0);
    }

    @Test
    void chaosMonkeyIsNotCalledByAspectsWithUnrelatedPointcuts() {
        watcherProperties.setService(true);
        watcherProperties.setComponent(true);
        watcherProperties.setController(true);
        watcherProperties.setRepository(true);
        watcherProperties.setRestController(true);

        addNonRelevantAspects();

        callTargetMethod();

        verifyDependenciesCalledXTimes(0);
    }

    private void addRelevantAspect() {
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        factory.addAspect(serviceAspect);
    }

    private void addNonRelevantAspects() {
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringComponentAspect componentAspect = new SpringComponentAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringRepositoryStereotypeAspect repositoryStereotypeAspect = new SpringRepositoryStereotypeAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);

        factory.addAspect(controllerAspect);
        factory.addAspect(componentAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(repositoryStereotypeAspect);
    }

    private void callTargetMethod() {
        DemoService proxy = factory.getProxy();
        proxy.sayHello();
    }

    private void verifyDependenciesCalledXTimes(int i) {
        verify(chaosMonkeyRequestScopeMock, times(i)).callChaosMonkey(simpleName);
        verify(metricsMock, times(i)).publishMetricEvent(pointcutName, MetricType.SERVICE);
        verifyNoMoreInteractions(chaosMonkeyRequestScopeMock, metricsMock);
    }
}
