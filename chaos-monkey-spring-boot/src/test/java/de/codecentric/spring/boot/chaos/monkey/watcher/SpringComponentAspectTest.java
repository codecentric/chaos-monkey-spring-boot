package de.codecentric.spring.boot.chaos.monkey.watcher;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;

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
        SpringComponentAspect componentAspect = new SpringComponentAspect(chaosMonkeyMock);
        factory.addAspect(componentAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoComponent target = new DemoComponent();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(restControllerAspect);

        DemoComponent proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

}