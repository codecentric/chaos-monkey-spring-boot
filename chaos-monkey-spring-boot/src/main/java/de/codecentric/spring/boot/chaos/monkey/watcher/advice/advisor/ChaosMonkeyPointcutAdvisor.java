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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class ChaosMonkeyPointcutAdvisor extends DefaultPointcutAdvisor {

    public ChaosMonkeyPointcutAdvisor(ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter, Advice advice, ClassFilter classFilter) {
        this(chaosMonkeyBaseClassFilter, advice, classFilter, MethodMatcher.TRUE);
    }

    public ChaosMonkeyPointcutAdvisor(ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter, Advice advice, ClassFilter classFilter,
            MethodMatcher methodFilter) {
        super(new ComposablePointcut(ClassFilters.intersection(chaosMonkeyBaseClassFilter, classFilter), methodFilter), advice);
    }
}
