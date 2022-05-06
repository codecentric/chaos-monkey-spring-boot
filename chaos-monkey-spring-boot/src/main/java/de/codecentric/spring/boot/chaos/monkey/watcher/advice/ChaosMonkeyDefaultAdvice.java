/*
 * Copyright 2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
public class ChaosMonkeyDefaultAdvice extends AbstractChaosMonkeyAdvice {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
    @Nullable
    private final MetricEventPublisher metricEventPublisher;

    private final ChaosTarget target;

    private final Predicate<ProceedingJoinPoint> isEnabled;

    public ChaosMonkeyDefaultAdvice(ChaosMonkeyRequestScope chaosMonkeyRequestScope, @Nullable MetricEventPublisher metricEventPublisher,
            ChaosTarget target, BooleanSupplier isEnabled) {
        this(chaosMonkeyRequestScope, metricEventPublisher, target, (pjp) -> isEnabled.getAsBoolean());
    }

    @Override
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        if (isEnabled.test(pjp)) {
            log.debug("Watching public method on {} class: {}", target.getName().toLowerCase(Locale.ROOT), pjp.getSignature());

            if (metricEventPublisher != null) {
                metricEventPublisher.publishMetricEvent(calculatePointcut(pjp.toShortString()), target.getMetricType());
            }

            MethodSignature signature = (MethodSignature) pjp.getSignature();

            chaosMonkeyRequestScope.callChaosMonkey(target, createSignature(signature));
        }
        return pjp.proceed();
    }
}
