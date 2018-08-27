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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringServiceAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Mock
    private ChaosMonkeySettings chaosMonkeySettings;

    @Test
    public void chaosMonkeyIsCalled() {
        DemoService serviceTarget = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(serviceTarget);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, chaosMonkeySettings);
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
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalledWhenServiceNotWatched() {
        DemoService target = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, chaosMonkeySettings);
        factory.addAspect(controllerAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);

        final List<String> watchedServices = new ArrayList<>();
        watchedServices.add("TestService");

        when(chaosMonkeySettings.getAssaultProperties())
            .thenReturn(assaultPropertyWithWatchedServices(watchedServices));

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);
    }

    @Test
    public void chaosMonkeyIsCalledWhenServiceWatched() {
        DemoService target = new DemoService();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, chaosMonkeySettings);
        factory.addAspect(controllerAspect);
        factory.addAspect(restControllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);

        final List<String> watchedServices = new ArrayList<>();
        watchedServices.add("DemoService");

        when(chaosMonkeySettings.getAssaultProperties())
            .thenReturn(assaultPropertyWithWatchedServices(watchedServices));

        DemoService proxy = factory.getProxy();
        proxy.sayHelloService();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);
    }

    private AssaultProperties assaultPropertyWithWatchedServices(List<String> watchedServices) {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setWatchedServices(watchedServices);
        return assaultProperties;
    }
}