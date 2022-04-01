package de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import java.util.Collections;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

class ChaosMonkeyAnnotationPointcutAdvisorTest {

  private final Advice advice = mock(Advice.class);
  private final ChaosMonkeyBaseClassFilter baseClassFilter = mock(ChaosMonkeyBaseClassFilter.class);

  @Test
  public void shouldMatchWhenAnnotationIsPresent() {
    when(baseClassFilter.matches(any())).thenReturn(true);
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyAnnotationPointcutAdvisor(
            baseClassFilter, advice, Component.class, MethodMatcher.TRUE);
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), AnnotatedClass.class))
        .hasSize(1);
  }

  @Test
  public void shouldNotMatchWhenAnnotationIsAbsent() {
    when(baseClassFilter.matches(any())).thenReturn(true);
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyAnnotationPointcutAdvisor(
            baseClassFilter, advice, Component.class, MethodMatcher.TRUE);
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), NotAnnotatedClass.class))
        .isEmpty();
  }

  @Component
  public static class AnnotatedClass {}

  public static class NotAnnotatedClass {}
}
