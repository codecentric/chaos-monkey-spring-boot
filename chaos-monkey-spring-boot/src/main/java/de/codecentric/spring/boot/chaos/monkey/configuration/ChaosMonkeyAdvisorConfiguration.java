/*
 * Copyright 2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyBeanPostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyDefaultAdvice;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyHealthIndicatorAdvice;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyAnnotationPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.MethodNameFilter;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.SpringHookMethodsFilter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@Configuration
public class ChaosMonkeyAdvisorConfiguration {
    private final WatcherProperties watcherProperties;

    @Bean
    public ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter() {
        return new ChaosMonkeyBaseClassFilter(watcherProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "controllerPointcutAdvisor")
    public ChaosMonkeyPointcutAdvisor controllerPointcutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        return new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.CONTROLLER, watcherProperties::isController),
                Controller.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = "restControllerPointcutAdvisor")
    public ChaosMonkeyPointcutAdvisor restControllerPointcutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        return new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.REST_CONTROLLER, watcherProperties::isRestController),
                RestController.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = "servicePointcutAdvisor")
    public ChaosMonkeyPointcutAdvisor servicePointcutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        return new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.SERVICE, watcherProperties::isService), Service.class,
                SpringHookMethodsFilter.INSTANCE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "componentPointcutAdvisor")
    public ChaosMonkeyPointcutAdvisor componentPointcutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        return new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.COMPONENT, watcherProperties::isComponent), Component.class,
                SpringHookMethodsFilter.INSTANCE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "jpaRepositoryPointcutAdvisor")
    @ConditionalOnClass(name = "org.springframework.data.repository.Repository")
    public ChaosMonkeyPointcutAdvisor jpaRepositoryPointcutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        val repositoryDefinition = (Class<? extends Annotation>) Class.forName("org.springframework.data.repository.RepositoryDefinition");
        Class<?> repository = Class.forName("org.springframework.data.repository.Repository");
        return new ChaosMonkeyPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.REPOSITORY, watcherProperties::isRepository),
                ClassFilters.union(new AnnotationClassFilter(repositoryDefinition, false), new RootClassFilter(repository)),
                SpringHookMethodsFilter.INSTANCE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "jdbcRepositoryPointcdutAdvisor")
    public ChaosMonkeyPointcutAdvisor jdbcRepositoryPointcdutAdvisor(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        return new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter,
                new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.REPOSITORY, watcherProperties::isRepository),
                Repository.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = "healthIndicatorAdviceProvider")
    @ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
    public ChaosMonkeyPointcutAdvisor healthIndicatorAdviceProvider(ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope)
            throws ClassNotFoundException {
        Class<?> healthIndicatorClass = Class.forName("org.springframework.boot.actuate.health.HealthIndicator");
        return new ChaosMonkeyPointcutAdvisor(baseClassFilter, new ChaosMonkeyHealthIndicatorAdvice(requestScope, watcherProperties),
                new RootClassFilter(healthIndicatorClass), new MethodNameFilter("getHealth"));
    }

    @Bean
    @ConditionalOnMissingBean
    public ChaosMonkeyBeanPostProcessor chaosMonkeyBeanPostProcessor(ChaosMonkeyRequestScope chaosMonkeyRequestScope,
            MetricEventPublisher publisher) {
        return new ChaosMonkeyBeanPostProcessor(watcherProperties, chaosMonkeyRequestScope, publisher);
    }
}
