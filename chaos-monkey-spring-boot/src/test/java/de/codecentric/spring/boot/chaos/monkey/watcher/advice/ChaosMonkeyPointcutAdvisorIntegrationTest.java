/*
 * Copyright 2020-2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyAnnotationPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.SpringHookMethodsFilter;
import de.codecentric.spring.boot.demo.chaos.monkey.component.ApplicationListenerComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.BeanPostProcessorComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.FactoryBeanComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.FinalDemoComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/** @author Kevin Sapper */
@SpringBootTest
class ChaosMonkeyPointcutAdvisorIntegrationTest {

    private static final String demoComponentPointcutName = "execution.DemoComponent.sayHello";
    private static final String finalDemoComponentPointcutName = "execution.FinalDemoComponent.sayHello";
    private static final String beanPostProcessorComponentPointcutName = "execution.BeanPostProcessorComponent.postProcessBeforeInitialization";
    private static final String applicationListenerComponentPointcutName = "execution.ApplicationListenerComponent.onApplicationEvent";
    private static final String beanFactorySingletonComponentPointcutName = "execution.FactoryBeanComponent.isSingleton";
    private static final String beanFactoryObjectTypeComponentPointcutName = "execution.FactoryBeanComponent.getObjectType";

    private static final String demoComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent.sayHello";
    private static final String finalDemoComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.FinalDemoComponent.sayHello";
    private static final String beanPostProcessorComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.BeanPostProcessorComponent.postProcessBeforeInitialization";
    private static final String applicationListenerComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.ApplicationListenerComponent.onApplicationEvent";
    private static final String beanFactorySingletonComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.BeanFactoryComponent.isSingleton";
    private static final String beanFactoryObjectTypeComponentSimpleName = "de.codecentric.spring.boot.demo.chaos.monkey.component.BeanFactoryComponent.getObjectType";

    @Autowired
    DemoComponent demoComponent;

    @Autowired
    FinalDemoComponent finalDemoComponent;

    @Autowired
    BeanPostProcessorComponent beanPostProcessorComponent;

    @Autowired
    ApplicationListenerComponent applicationListenerComponent;

    @Autowired
    ChaosMonkeyRequestScope chaosMonkeyRequestScopeMock;

    @Autowired
    MetricEventPublisher metricsMock;

    @Autowired
    FactoryBeanComponent factoryBeanComponent;

    @Test
    public void chaosMonkeyIsCalledWhenComponentIsNotFinal() {
        demoComponent.sayHello();
        verify(chaosMonkeyRequestScopeMock, times(1)).callChaosMonkey(ChaosTarget.COMPONENT, demoComponentSimpleName);
        verify(metricsMock, times(1)).publishMetricEvent(demoComponentPointcutName, MetricType.COMPONENT);
    }

    @Test
    public void chaosMonkeyIsNotCalledWhenComponentIsFinal() {
        finalDemoComponent.sayHello();
        verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(ChaosTarget.COMPONENT, finalDemoComponentSimpleName);
        verify(metricsMock, times(0)).publishMetricEvent(finalDemoComponentPointcutName, MetricType.COMPONENT);
    }

    @Test
    public void chaosMonkeyDoesNotProxyIgnoredSpringInterfaces() {
        beanPostProcessorComponent.postProcessBeforeInitialization(new Object(), "fakeBean");
        applicationListenerComponent.onApplicationEvent(mock(ApplicationEvent.class));
        factoryBeanComponent.getObject();

        verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(null, beanPostProcessorComponentSimpleName);
        verify(metricsMock, times(0)).publishMetricEvent(beanPostProcessorComponentPointcutName, MetricType.COMPONENT);

        verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(null, applicationListenerComponentSimpleName);
        verify(metricsMock, times(0)).publishMetricEvent(applicationListenerComponentPointcutName, MetricType.COMPONENT);

        verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(null, beanFactorySingletonComponentSimpleName);
        verify(chaosMonkeyRequestScopeMock, times(0)).callChaosMonkey(null, beanFactoryObjectTypeComponentSimpleName);
        verify(metricsMock, times(0)).publishMetricEvent(beanFactorySingletonComponentPointcutName, MetricType.COMPONENT);
        verify(metricsMock, times(0)).publishMetricEvent(beanFactoryObjectTypeComponentPointcutName, MetricType.COMPONENT);
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
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
        ChaosMonkeyPointcutAdvisor advisor() {
            WatcherProperties watcherProperties = new WatcherProperties();
            watcherProperties.setComponent(true);
            return new ChaosMonkeyAnnotationPointcutAdvisor(new ChaosMonkeyBaseClassFilter(watcherProperties),
                    new ChaosMonkeyDefaultAdvice(chaosMonkeyRequestScopeMock(), metricsMock(), ChaosTarget.COMPONENT, watcherProperties::isComponent),
                    Component.class, SpringHookMethodsFilter.INSTANCE);
        }

        @Bean
        DemoComponent demoComponent() {
            return mock(DemoComponent.class);
        }

        @Bean
        FinalDemoComponent finalDemoComponent() {
            return mock(FinalDemoComponent.class);
        }

        @Bean
        BeanPostProcessorComponent beanPostProcessorComponent() {
            return mock(BeanPostProcessorComponent.class);
        }

        @Bean
        FactoryBeanComponent factoryBeanComponent() {
            return mock(FactoryBeanComponent.class);
        }

        @Bean
        ApplicationListenerComponent applicationListenerComponent() {
            return mock(ApplicationListenerComponent.class);
        }
    }
}
