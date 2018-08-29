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
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
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


    @Test
    public void chaosMonkeyIsCalled() {
        DemoComponent target = new DemoComponent();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringComponentAspect componentAspect = new SpringComponentAspect(chaosMonkeyMock, null);
        factory.addAspect(componentAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey("de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello");
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoComponent target = new DemoComponent();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock, null);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock, null);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock, null);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock, null);
        factory.addAspect(controllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(restControllerAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey("de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello");
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

}