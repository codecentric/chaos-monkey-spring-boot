package de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class ChaosMonkeyPointcutAdvisor extends DefaultPointcutAdvisor {

  public ChaosMonkeyPointcutAdvisor(
      ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter,
      Advice advice,
      ClassFilter classFilter) {
    this(chaosMonkeyBaseClassFilter, advice, classFilter, MethodMatcher.TRUE);
  }

  public ChaosMonkeyPointcutAdvisor(
      ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter,
      Advice advice,
      ClassFilter classFilter,
      MethodMatcher methodFilter) {
    super(
        new ComposablePointcut(
            ClassFilters.intersection(chaosMonkeyBaseClassFilter, classFilter), methodFilter),
        advice);
  }
}
