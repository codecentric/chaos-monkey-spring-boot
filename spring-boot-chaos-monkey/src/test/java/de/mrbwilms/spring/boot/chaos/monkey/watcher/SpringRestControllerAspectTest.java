package de.mrbwilms.spring.boot.chaos.monkey.watcher;

import de.mrbwilms.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.mrbwilms.spring.boot.chaos.monkey.demo.restcontroller.DemoRestController;
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
public class SpringRestControllerAspectTest {

    @Mock
    private ChaosMonkey chaosMonkeyMock;

    @Test
    public void chaosMonkeyIsCalled() {
        DemoRestController target = new DemoRestController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringRestControllerAspect serviceAspect = new SpringRestControllerAspect(chaosMonkeyMock);
        factory.addAspect(serviceAspect);

        DemoRestController proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(1)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }

    @Test
    public void chaosMonkeyIsNotCalled() {
        DemoRestController target = new DemoRestController();

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        SpringControllerAspect controllerAspect = new SpringControllerAspect(chaosMonkeyMock);
        SpringServiceAspect serviceAspect = new SpringServiceAspect(chaosMonkeyMock);
        SpringRepositoryAspect repositoryAspect = new SpringRepositoryAspect(chaosMonkeyMock);
        factory.addAspect(controllerAspect);
        factory.addAspect(serviceAspect);
        factory.addAspect(repositoryAspect);


        DemoRestController proxy = factory.getProxy();
        proxy.sayHello();

        verify(chaosMonkeyMock, times(0)).callChaosMonkey();
        verifyNoMoreInteractions(chaosMonkeyMock);

    }
}