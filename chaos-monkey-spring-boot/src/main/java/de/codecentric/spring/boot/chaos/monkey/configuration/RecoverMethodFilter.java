/*
 * Copyright 2025 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.retry.annotation.Recover;

import java.lang.reflect.Method;

public class RecoverMethodFilter implements MethodFilter {
    @Override
    public boolean filter(Object target, Method method) {
        Recover recover = AnnotatedElementUtils.findMergedAnnotation(target.getClass(), Recover.class);
        if (recover == null) {
            recover = findAnnotationOnTarget(target, method);
        }
        return recover != null;
    }

    private Recover findAnnotationOnTarget(Object target, Method method) {
        try {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            return AnnotatedElementUtils.findMergedAnnotation(targetMethod, Recover.class);
        } catch (RuntimeException | NoSuchMethodException e) {
            return null;
        }
    }
}
