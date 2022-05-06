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
package de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter;

import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.lang.reflect.Modifier;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.web.filter.GenericFilterBean;

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
                !hasProblematicFinalMethod(clazz);
    }

    private boolean inChaosMonkeyPackage(Class<?> clazz) {
        return clazz.getName().startsWith("de.codecentric.spring.boot.chaos.monkey.");
    }

    private boolean inSpringCloudContextPackage(Class<?> clazz) {
        return clazz.getName().startsWith("org.springframework.cloud.context.");
    }

    private boolean nonFinalOrJdkProxiedClass(Class<?> clazz) {
        return !Modifier.isFinal(clazz.getModifiers()) || clazz.getName().startsWith("com.sun.proxy.");
    }

    private boolean hasProblematicFinalMethod(Class<?> clazz) {
        try {
            // see https://github.com/codecentric/chaos-monkey-spring-boot/pull/261
            return GenericFilterBean.class.isAssignableFrom(clazz);
        } catch (NoClassDefFoundError error) {
            // if a class implements an interface not on the classpath, isAssignableFrom
            // will fail.
            return false;
        }
    }
}
