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

package de.codecentric.spring.boot.chaos.monkey.watcher.aspect;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/** @author Benjamin Wilms */
@Aspect
@AllArgsConstructor
@Slf4j
public class SpringComponentAspect extends ChaosMonkeyBaseAspect {

  private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;

  private MetricEventPublisher metricEventPublisher;

  private WatcherProperties watcherProperties;

  @Pointcut("within(@org.springframework.stereotype.Component *)")
  public void classAnnotatedWithComponentPointcut() {}

  @Pointcut("within(org.springframework.cloud.context..*)")
  public void classInSpringCloudContextPackage() {}

  @Around(
      "classAnnotatedWithComponentPointcut() && !classInSpringCloudContextPackage() "
          + "&& allPublicMethodPointcut() && !classInChaosMonkeyPackage() && !springHooksPointcut()")
  public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
    if (watcherProperties.isComponent()) {
      log.debug("Watching public method on component class: {}", pjp.getSignature());

      if (metricEventPublisher != null) {
        metricEventPublisher.publishMetricEvent(
            calculatePointcut(pjp.toShortString()), MetricType.COMPONENT);
      }

      MethodSignature signature = (MethodSignature) pjp.getSignature();

      chaosMonkeyRequestScope.callChaosMonkey(ChaosTarget.COMPONENT, createSignature(signature));
    }
    return pjp.proceed();
  }
}
