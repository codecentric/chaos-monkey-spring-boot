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
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyPointcutAdvisor(
            baseClassFilter, advice, ClassFilter.TRUE, MethodMatcher.TRUE);
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class))
        .hasSize(1);
  }

  @Test
  public void shouldNotMatchWhenBaseFilterFails() {
    when(baseClassFilter.matches(any())).thenReturn(false);
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyPointcutAdvisor(
            baseClassFilter, advice, ClassFilter.TRUE, MethodMatcher.TRUE);
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class))
        .isEmpty();
  }

  @Test
  public void shouldNotMatchWhenClassFilterFails() {
    when(baseClassFilter.matches(any())).thenReturn(true);
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyPointcutAdvisor(
            baseClassFilter, advice, (clazz) -> false, MethodMatcher.TRUE);
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class))
        .isEmpty();
  }

  @Test
  public void shouldNotMatchWhenMethodFilterFails() {
    when(baseClassFilter.matches(any())).thenReturn(true);
    ChaosMonkeyPointcutAdvisor advisor =
        new ChaosMonkeyPointcutAdvisor(
            baseClassFilter,
            advice,
            ClassFilter.TRUE,
            new StaticMethodMatcher() {
              @Override
              public boolean matches(Method method, Class<?> targetClass) {
                return false;
              }
            });
    assertThat(
            AopUtils.findAdvisorsThatCanApply(
                Collections.singletonList(advisor), ChaosMonkeyPointcutAdvisorTest.class))
        .isEmpty();
  }
}
