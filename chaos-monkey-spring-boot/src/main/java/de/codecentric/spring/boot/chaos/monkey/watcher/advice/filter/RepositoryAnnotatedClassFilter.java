/*
 * Copyright 2023 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ClassFilter;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class RepositoryAnnotatedClassFilter implements ClassFilter {
    private static final Logger log = LoggerFactory.getLogger(RepositoryAnnotatedClassFilter.class);

    @Override
    public boolean matches(Class<?> clazz) {
        if(clazz.isAnnotationPresent(Repository.class)
                // if a repository is proxied by spring the annotation can only be found on the
                // implemented interface
                || Proxy.isProxyClass(clazz) && Arrays.stream(clazz.getInterfaces()).anyMatch(i -> i.isAnnotationPresent(Repository.class))){

            log.info("Repository class which is matches RepositoryAnnotationFilter condition"+ clazz);
        }

        return clazz.isAnnotationPresent(Repository.class)
                // if a repository is proxied by spring the annotation can only be found on the
                // implemented interface
                || Proxy.isProxyClass(clazz) && Arrays.stream(clazz.getInterfaces()).anyMatch(i -> i.isAnnotationPresent(Repository.class));
    }
}
