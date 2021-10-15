package de.codecentric.spring.boot.chaos.monkey.watcher.aspect;

import static org.mockito.Mockito.*;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.bean.DemoBean;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChaosMonkeyBeanPostProcessorTest {

  private DemoBean target = new DemoBean();

  private WatcherProperties watcherProperties = new WatcherProperties();

  @Mock private ChaosMonkeyRequestScope requestScope;

  @Mock private MetricEventPublisher metrics;

  private ChaosMonkeyBeanPostProcessor postProcessor;

  private String pointcutName = "execution.DemoBean.sayHello";

  private String simpleName = "de.codecentric.spring.boot.demo.chaos.monkey.bean.DemoBean.sayHello";

  @BeforeEach
  void setup() {
    postProcessor = new ChaosMonkeyBeanPostProcessor(watcherProperties, requestScope, metrics);
  }

  @Test
  void chaosMonkeyIsCalledWhenEnabledInConfig() {
    watcherProperties.setBeans(Collections.singletonList("demoBean"));

    callTargetMethod();

    verifyDependenciesCalledXTimes(1);
  }

  @Test
  void chaosMonkeyIsNotCalledWhenDisabledInConfig() {
    watcherProperties.setBeans(Collections.emptyList());

    callTargetMethod();

    verifyDependenciesCalledXTimes(0);
  }

  @Test
  void chaosMonkeyIsNotCalledWithUnrelatedBeansInConfig() {
    watcherProperties.setBeans(Collections.singletonList("demoComponent"));

    callTargetMethod();

    verifyDependenciesCalledXTimes(0);
  }

  private void callTargetMethod() {
    DemoBean proxy = (DemoBean) postProcessor.postProcessAfterInitialization(target, "demoBean");
    proxy.sayHello();
  }

  private void verifyDependenciesCalledXTimes(int i) {
    verify(requestScope, times(i)).callChaosMonkey(ChaosTarget.BEAN, simpleName);
    verify(metrics, times(i)).publishMetricEvent(pointcutName, MetricType.BEAN);
    verifyNoMoreInteractions(requestScope, metrics);
  }
}
