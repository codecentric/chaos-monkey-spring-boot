package de.codecentric.spring.boot.chaos.monkey.watcher;

import de.codecentric.spring.boot.chaos.monkey.demo.controller.DemoController;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringControllerAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Test
    public void chaosMonkeyIsCalled() {
        DemoController target = new DemoController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        factory.addAspect(controllerAspect);

        DemoController proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoController target = new DemoController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock);
        SpringRestControllerAspect restControllerAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        factory.addAspect(repositoryAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(restControllerAspect);

        DemoController proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }
}