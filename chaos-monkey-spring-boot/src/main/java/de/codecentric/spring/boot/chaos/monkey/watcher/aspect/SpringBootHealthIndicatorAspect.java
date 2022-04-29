/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.actuate.health.Health;

@Aspect
@AllArgsConstructor
@Slf4j
public class SpringBootHealthIndicatorAspect extends ChaosMonkeyBaseAspect {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    private final WatcherProperties watcherProperties;

    @Pointcut("execution(* org.springframework.boot.actuate.health.HealthIndicator.getHealth(..))")
    public void getHealthPointCut() {
    }

    @Around("getHealthPointCut() && !classInChaosMonkeyPackage()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        Health health = (Health) pjp.proceed();
        if (watcherProperties.isActuatorHealth()) {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            try {
                this.chaosMonkeyRequestScope.callChaosMonkey(ChaosTarget.ACTUATOR_HEALTH, createSignature(signature));
            } catch (final Exception e) {
                log.error("Exception occurred", e);
                health = Health.down(e).build();
            }
        }
        return health;
    }
}
