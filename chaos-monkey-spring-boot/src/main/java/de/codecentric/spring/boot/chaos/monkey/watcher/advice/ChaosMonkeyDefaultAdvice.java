package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
@Slf4j
public class ChaosMonkeyDefaultAdvice extends AbstractChaosMonkeyAdvice {

  private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
  @Nullable private final MetricEventPublisher metricEventPublisher;
  private final WatcherProperties watcherProperties;

  private final ChaosTarget target;

  @Override
  public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
    if (isEnabled(pjp)) {
      log.debug(
          "Watching public method on {} class: {}",
          target.getName().toLowerCase(Locale.ROOT),
          pjp.getSignature());

      if (metricEventPublisher != null) {
        metricEventPublisher.publishMetricEvent(
            calculatePointcut(pjp.toShortString()), target.getMetricType());
      }

      MethodSignature signature = (MethodSignature) pjp.getSignature();

      chaosMonkeyRequestScope.callChaosMonkey(target, createSignature(signature));
    }
    return pjp.proceed();
  }

  public boolean isEnabled(ProceedingJoinPoint pjp) {
    return target.isEnabled(watcherProperties);
  }
}
