package de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor;

import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import java.lang.annotation.Annotation;
import org.aopalliance.aop.Advice;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.annotation.AnnotationClassFilter;

public class ChaosMonkeyAnnotationPointcutAdvisor extends ChaosMonkeyPointcutAdvisor {

  public ChaosMonkeyAnnotationPointcutAdvisor(
      ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter,
      Advice advice,
      Class<? extends Annotation> annotation) {
    this(chaosMonkeyBaseClassFilter, advice, annotation, MethodMatcher.TRUE);
  }

  public ChaosMonkeyAnnotationPointcutAdvisor(
      ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter,
      Advice advice,
      Class<? extends Annotation> annotation,
      MethodMatcher methodFilter) {
    super(chaosMonkeyBaseClassFilter, advice, new AnnotationClassFilter(annotation), methodFilter);
  }
}
