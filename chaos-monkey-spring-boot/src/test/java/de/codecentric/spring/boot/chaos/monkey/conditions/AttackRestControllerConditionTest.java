/*
 * Copyright 2018 the original author or authors.
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

package de.codecentric.spring.boot.chaos.monkey.conditions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AttackRestControllerConditionTest {

    private final String CHAOS_MONKEY_CONTROL_CONDITION = "chaos.monkey.watcher.rest-controller";
    private final String FALSE_DEFAULT = "false";
    private AttackRestControllerCondition attackRestControllerCondition;
    @Mock
    private ConditionContext conditionContext;
    @Mock
    private AnnotatedTypeMetadata annotatedTypeMetadata;
    @Mock
    private Environment environment;

    @Before
    public void setup() {
        given(conditionContext.getEnvironment()).willReturn(environment);

        attackRestControllerCondition = new AttackRestControllerCondition();
    }

    @Test
    public void doesNotMatchWhenConditionIsFalse() {
        given(environment.getProperty(CHAOS_MONKEY_CONTROL_CONDITION, FALSE_DEFAULT)).willReturn("false");

        boolean matches = attackRestControllerCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isFalse();
    }

    @Test
    public void doesNotMatchWhenConditionIsNotABoolean() {
        given(environment.getProperty(CHAOS_MONKEY_CONTROL_CONDITION, FALSE_DEFAULT)).willReturn("notBoolean");

        boolean matches = attackRestControllerCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isFalse();
    }

    @Test
    public void matchesWhenConditionIsTrue() {
        given(environment.getProperty(CHAOS_MONKEY_CONTROL_CONDITION, FALSE_DEFAULT)).willReturn("true");

        boolean matches = attackRestControllerCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isTrue();
    }
}
