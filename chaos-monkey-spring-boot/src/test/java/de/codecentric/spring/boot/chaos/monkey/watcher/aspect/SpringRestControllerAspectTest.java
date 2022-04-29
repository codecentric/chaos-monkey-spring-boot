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
package de.codecentric.spring.boot.chaos.monkey.watcher.aspect;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.restcontroller.DemoRestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/** @author Benjamin Wilms */
@ExtendWith(MockitoExtension.class)
class SpringRestControllerAspectTest {

    private DemoRestController target = new DemoRestController();

    private WatcherProperties watcherProperties = new WatcherProperties();

    private AspectJProxyFactory factory = new AspectJProxyFactory(target);

    @Mock
    private ChaosMonkeyRequestScope chaosMonkeyRequestScopeMock;

    @Mock
    private MetricEventPublisher metricsMock;

    private String pointcutName = "execution.DemoRestController.sayHello";

    private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.restcontroller.DemoRestController.sayHello";

    @Test
    void chaosMonkeyIsCalledWhenEnabledInConfig() {
        watcherProperties.setRestController(true);

        addRelevantAspect();

        callTargetMethod();

        verifyDependenciesCalledXTimes(1);
    }

    @Test
    void chaosMonkeyIsNotCalledWhenDisabledInConfig() {
        watcherProperties.setController(false);

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
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        factory.addAspect(restControllerAspect);
    }

    private void addNonRelevantAspects() {
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringComponentAspect componentAspect = new SpringComponentAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringRepositoryAspectJPA repositoryAspect = new SpringRepositoryAspectJPA(chaosMonkeyRequestScopeMock, metricsMock, watcherProperties);
        SpringRepositoryAspectJDBC repositoryStereotypeAspect = new SpringRepositoryAspectJDBC(chaosMonkeyRequestScopeMock, metricsMock,
                watcherProperties);

        factory.addAspect(controllerAspect);
        factory.addAspect(componentAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(repositoryStereotypeAspect);
    }

    private void callTargetMethod() {
        DemoRestController proxy = factory.getProxy();
        proxy.sayHello();
    }

    private void verifyDependenciesCalledXTimes(int i) {
        verify(chaosMonkeyRequestScopeMock, times(i)).callChaosMonkey(ChaosTarget.REST_CONTROLLER, simpleName);
        verify(metricsMock, times(i)).publishMetricEvent(pointcutName, MetricType.RESTCONTROLLER);
        verifyNoMoreInteractions(chaosMonkeyRequestScopeMock, metricsMock);
    }
}
