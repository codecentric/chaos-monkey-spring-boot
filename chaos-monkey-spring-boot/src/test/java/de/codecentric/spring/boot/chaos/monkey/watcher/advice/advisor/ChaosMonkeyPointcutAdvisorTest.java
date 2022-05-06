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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import java.lang.reflect.Method;
import java.util.Collections;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;

class ChaosMonkeyPointcutAdvisorTest {

    private final Advice advice = mock(Advice.class);
    private final ChaosMonkeyBaseClassFilter baseClassFilter = mock(ChaosMonkeyBaseClassFilter.class);

    @Test
    public void shouldMatchWhenAllFiltersMatch() {
        when(baseClassFilter.matches(any())).thenReturn(true);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyPointcutAdvisor(baseClassFilter, advice, ClassFilter.TRUE, MethodMatcher.TRUE);
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class)).hasSize(1);
    }

    @Test
    public void shouldNotMatchWhenBaseFilterFails() {
        when(baseClassFilter.matches(any())).thenReturn(false);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyPointcutAdvisor(baseClassFilter, advice, ClassFilter.TRUE, MethodMatcher.TRUE);
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class)).isEmpty();
    }

    @Test
    public void shouldNotMatchWhenClassFilterFails() {
        when(baseClassFilter.matches(any())).thenReturn(true);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyPointcutAdvisor(baseClassFilter, advice, (clazz) -> false, MethodMatcher.TRUE);
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class)).isEmpty();
    }

    @Test
    public void shouldNotMatchWhenMethodFilterFails() {
        when(baseClassFilter.matches(any())).thenReturn(true);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyPointcutAdvisor(baseClassFilter, advice, ClassFilter.TRUE, new StaticMethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return false;
            }
        });
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class)).isEmpty();
    }
}
