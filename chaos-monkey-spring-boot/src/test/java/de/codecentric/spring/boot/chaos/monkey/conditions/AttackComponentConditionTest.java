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

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class AttackComponentConditionTest {
    private AttackComponentCondition attackComponentCondition;
    private final String CHAOS_MONKEY_COMPONENT_CONDITION = "chaos.monkey.watcher.component";
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

        attackComponentCondition = new AttackComponentCondition();
    }

    @Test
    public void doesNotMatchWhenConditionIsFalse() {
        given(environment.getProperty(CHAOS_MONKEY_COMPONENT_CONDITION, FALSE_DEFAULT)).willReturn("false");

        boolean matches = attackComponentCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isFalse();
    }

    @Test
    public void doesNotMatchWhenConditionIsNotABoolean() {
        given(environment.getProperty(CHAOS_MONKEY_COMPONENT_CONDITION, FALSE_DEFAULT)).willReturn("notBoolean");

        boolean matches = attackComponentCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isFalse();
    }

    @Test
    public void matchesWhenConditionIsTrue() {
        given(environment.getProperty(CHAOS_MONKEY_COMPONENT_CONDITION, FALSE_DEFAULT)).willReturn("true");

        boolean matches = attackComponentCondition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(matches).isTrue();
    }

}