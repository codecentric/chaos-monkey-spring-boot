/*
 * Copyright 2020 the original author or authors.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.FinalDemoComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/** @author Kevin Sapper */
@SpringBootTest
class SpringComponentAspectIntegrationTest {

  private static final String demoComponentPointcutName = "execution.DemoComponent.sayHello";
  private static final String finalDemoComponentPointcutName =
      "execution.FinalDemoComponent.sayHello";

  private static final String demoComponentSimpleName =
      "de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello";
  private static final String finalDemoComponentSimpleName =
      "de.codecentric.spring.boot.demo.chaos.monkey.component.FinalDemoComponent.sayHello";

  @Autowired DemoComponent demoComponent;

  @Autowired FinalDemoComponent finalDemoComponent;

  @Autowired ChaosMonkeyRequestScope chaosMonkeyRequestScopeMock;

  @Autowired MetricEventPublisher metricsMock;

  @Test
  public void chaosMonkeyIsCalledWhenComponentIsNotFinal() {
    demoComponent.sayHello();
    verify(chaosMonkeyRequestScopeMock, times(1)).callChaosMonkey(demoComponentSimpleName);
    verify(metricsMock, times(1))
        .publishMetricEvent(demoComponentPointcutName, MetricType.COMPONENT);
  }

  @Test
  public void chaosMonkeyIsNotCalledWhenComponentIsFinal() {
    finalDemoComponent.sayHello();
    verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(finalDemoComponentSimpleName);
    verify(metricsMock, times(0))
        .publishMetricEvent(finalDemoComponentPointcutName, MetricType.COMPONENT);
  }

  @Configuration
  @EnableAspectJAutoProxy
  public static class TestContext {

    @Bean
    public ChaosMonkeyRequestScope chaosMonkeyRequestScopeMock() {
      return mock(ChaosMonkeyRequestScope.class);
    }

    @Bean
    public MetricEventPublisher metricsMock() {
      return mock(MetricEventPublisher.class);
    }

    @Bean
    SpringComponentAspect aspect() {
      WatcherProperties watcherProperties = new WatcherProperties();
      watcherProperties.setComponent(true);
      return new SpringComponentAspect(
          chaosMonkeyRequestScopeMock(), metricsMock(), watcherProperties);
    }

    @Bean
    DemoComponent demoComponent() {
      return mock(DemoComponent.class);
    }

    @Bean
    FinalDemoComponent finalDemoComponent() {
      return mock(FinalDemoComponent.class);
    }
  }
}
