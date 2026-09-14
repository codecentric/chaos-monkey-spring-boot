/*
 * Copyright 2022-2026 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter;

import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

@RequiredArgsConstructor
public class ChaosMonkeyBaseClassFilter implements ClassFilter {
    private final WatcherProperties watcherProperties;

    @Override
    public boolean matches(Class<?> clazz) {
        return
        // exclude as configured by user
        watcherProperties.getExcludeClasses().stream().noneMatch(exclude -> exclude.isAssignableFrom(clazz)) &&
        // don't attack ourselves
                !inChaosMonkeyPackage(clazz) &&
                // see https://github.com/codecentric/chaos-monkey-spring-boot/pull/120
                !inSpringCloudContextPackage(clazz) &&
                // can't proxy final classes
                nonFinalOrJdkProxiedClass(clazz) &&
                // see https://github.com/codecentric/chaos-monkey-spring-boot/issues/287
                // and https://github.com/codecentric/chaos-monkey-spring-boot/issues/595
                !hasProblematicFinalMethod(clazz);
    }

    private boolean inChaosMonkeyPackage(Class<?> clazz) {
        return clazz.getName().startsWith("de.codecentric.spring.boot.chaos.monkey.");
    }

    private boolean inSpringCloudContextPackage(Class<?> clazz) {
        return clazz.getName().startsWith("org.springframework.cloud.context.");
    }

    private boolean nonFinalOrJdkProxiedClass(Class<?> clazz) {
        return !Modifier.isFinal(clazz.getModifiers()) || Proxy.isProxyClass(clazz);
    }

    private boolean hasProblematicFinalMethod(Class<?> clazz) {
        // only stereotype beans (@Component, @Service, @Controller, @RestController,
        // @Repository) are affected, framework types like AbstractHealthIndicator
        // must stay proxyable
        if (Proxy.isProxyClass(clazz) || AnnotationUtils.findAnnotation(clazz, Component.class) == null) {
            return false;
        }
        try {
            // A final method runs on a CGLIB proxy itself rather than its injected
            // target, see
            // https://github.com/codecentric/chaos-monkey-spring-boot/issues/595
            for (Class<?> type = clazz; type != null && type != Object.class; type = type.getSuperclass()) {
                for (Method method : type.getDeclaredMethods()) {
                    int modifiers = method.getModifiers();
                    // private and static methods are never proxied, so they are not affected
                    if (Modifier.isFinal(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NoClassDefFoundError error) {
            // if a method signature references a class not on the classpath,
            // getDeclaredMethods will fail.
            return false;
        }
    }
}
