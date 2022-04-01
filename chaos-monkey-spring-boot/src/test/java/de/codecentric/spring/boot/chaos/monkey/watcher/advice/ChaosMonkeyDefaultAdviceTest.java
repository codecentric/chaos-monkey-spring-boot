package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyCreatorSupport;

class ChaosMonkeyDefaultAdviceTest {

  private final WatcherProperties watcherProperties = new WatcherProperties();
  private final ChaosMonkeyRequestScope requestScope = mock(ChaosMonkeyRequestScope.class);
  private final MetricEventPublisher eventPublisher = mock(MetricEventPublisher.class);
  private final Advice advice =
      new ChaosMonkeyDefaultAdvice(
          requestScope, eventPublisher, watcherProperties, ChaosTarget.COMPONENT);
  private DemoComponent proxy;

  @BeforeEach
  public void setup() {
    ProxyCreatorSupport proxyCreator = new ProxyCreatorSupport();
    proxyCreator.addAdvice(advice);
    proxyCreator.setTarget(new DemoComponent());
    proxy =
        (DemoComponent) proxyCreator.getAopProxyFactory().createAopProxy(proxyCreator).getProxy();
    reset(requestScope, eventPublisher);
  }

  @Test
  public void shouldCallChaosMonkeyIfEnabled() {
    watcherProperties.setComponent(true);

    proxy.sayHello();

    verify(requestScope, times(1))
        .callChaosMonkey(
            ChaosTarget.COMPONENT,
            "de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello");
    verify(eventPublisher, times(1))
        .publishMetricEvent("execution.DemoComponent.sayHello", MetricType.COMPONENT);
    verifyNoMoreInteractions(requestScope, eventPublisher);
  }

  @Test
  public void shouldNotCallChaosMonkeyIfDisabled() {
    watcherProperties.setComponent(false);

    proxy.sayHello();

    verifyNoInteractions(requestScope, eventPublisher);
  }
}
