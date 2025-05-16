/*
 * Copyright 2023-2025 the original author or authors.
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

import jakarta.annotation.Nonnull;
import org.springframework.aop.ClassFilter;

import java.lang.reflect.Proxy;

public class RepositoryClassFilter implements ClassFilter {
    private static final String SPRING_DATA_REPOSITORY_CLASS_REF = "org.springframework.data.repository.Repository";

    private final Class<?> repositoryClass;

    public RepositoryClassFilter() throws ClassNotFoundException {
        repositoryClass = Class.forName(SPRING_DATA_REPOSITORY_CLASS_REF);
    }

    @Override
    public boolean matches(@Nonnull Class<?> clazz) {
        return Proxy.isProxyClass(clazz) && repositoryClass.isAssignableFrom(clazz);
    }

    @Override
    public String toString() {
        return "RepositoryClassFilter{repositoryClass=" + SPRING_DATA_REPOSITORY_CLASS_REF + '}';
    }

    @Override
    public final boolean equals(Object other) {
        return other instanceof RepositoryClassFilter;
    }

    @Override
    public int hashCode() {
        return SPRING_DATA_REPOSITORY_CLASS_REF.hashCode();
    }
}
