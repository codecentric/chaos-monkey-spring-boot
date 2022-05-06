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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.SpringHookMethodsFilter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
public class ChaosMonkeyBeanPostProcessor extends AbstractAdvisingBeanPostProcessor {

    private final WatcherProperties watcherProperties;
    private final Map<Object, String> activeBeanNameCache = new WeakHashMap<>();

    public ChaosMonkeyBeanPostProcessor(WatcherProperties watcherProperties, ChaosMonkeyRequestScope requestScope,
            MetricEventPublisher eventPublisher) {
        this.watcherProperties = watcherProperties;
        val advice = new ChaosMonkeyDefaultAdvice(requestScope, eventPublisher, ChaosTarget.BEAN,
                (pjp) -> watcherProperties.getBeans().contains(activeBeanNameCache.get(pjp.getThis())));
        this.advisor = new DefaultPointcutAdvisor(new ComposablePointcut(SpringHookMethodsFilter.INSTANCE), advice);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (watcherProperties.getBeans().contains(beanName)) {
            Object proxy = super.postProcessAfterInitialization(bean, beanName);
            activeBeanNameCache.put(proxy, beanName);
            return proxy;
        }
        return bean;
    }
}
