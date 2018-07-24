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

import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
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
    private Metrics metricsMock;

    @Test
    public void chaosMonkeyIsCalled() {
        DemoService serviceTarget = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(serviceTarget);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock);
        factory.addAspect(serviceAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoService target = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRestControllerAspect serviceAspect = new SpringRestControllerAspect(chaosMonkeyMock, metricsMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(repositoryAspect);


        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }
}