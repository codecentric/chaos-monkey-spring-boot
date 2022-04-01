package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.actuate.health.Health;

@RequiredArgsConstructor
@Slf4j
public class ChaosMonkeyHealthIndicatorAdvice extends AbstractChaosMonkeyAdvice {

  private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
  private final WatcherProperties watcherProperties;

  @Override
  public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
    Health health = (Health) pjp.proceed();
    if (watcherProperties.isActuatorHealth()) {
      MethodSignature signature = (MethodSignature) pjp.getSignature();
      try {
        this.chaosMonkeyRequestScope.callChaosMonkey(
            ChaosTarget.ACTUATOR_HEALTH, createSignature(signature));
      } catch (final Exception e) {
        log.error("Exception occurred", e);
        health = Health.down(e).build();
      }
    }
    return health;
  }
}
