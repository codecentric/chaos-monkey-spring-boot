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
public class AttackRepositoryConditionTest {

    private AttackRepositoryCondition attackRestControllerCondition;
    private final String CHAOS_MONKEY_CONTROL_CONDITION = "chaos.monkey.watcher.repository";
    private final String FALSE_DEFAULT = "false";

    @Mock
    private ConditionContext conditionContext;
    @Mock
    private AnnotatedTypeMetadata annotatedTypeMetadata;
    @Mock
    private Environment environment;

    @Before
    public void setup() {
        given(conditionContext.getEnvironment()).willReturn(environment);

        attackRestControllerCondition = new AttackRepositoryCondition();
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
