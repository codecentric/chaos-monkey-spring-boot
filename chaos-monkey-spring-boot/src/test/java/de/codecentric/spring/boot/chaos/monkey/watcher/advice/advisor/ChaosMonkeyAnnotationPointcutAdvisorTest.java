/*
 * Copyright 2022-2025 the original author or authors.
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import java.util.Collections;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

@ExtendWith(MockitoExtension.class)
class ChaosMonkeyAnnotationPointcutAdvisorTest {
    @Mock
    private Advice advice;
    @Mock
    private ChaosMonkeyBaseClassFilter baseClassFilter;

    @Test
    public void shouldMatchWhenAnnotationIsPresent() {
        when(baseClassFilter.matches(any())).thenReturn(true);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter, advice, Component.class, MethodMatcher.TRUE);
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), AnnotatedClass.class)).hasSize(1);
    }

    @Test
    public void shouldNotMatchWhenAnnotationIsAbsent() {
        when(baseClassFilter.matches(any())).thenReturn(true);
        ChaosMonkeyPointcutAdvisor advisor = new ChaosMonkeyAnnotationPointcutAdvisor(baseClassFilter, advice, Component.class, MethodMatcher.TRUE);
        assertThat(AopUtils.findAdvisorsThatCanApply(Collections.singletonList(advisor), NotAnnotatedClass.class)).isEmpty();
    }

    @Component
    public static class AnnotatedClass {
    }

    public static class NotAnnotatedClass {
    }
}
