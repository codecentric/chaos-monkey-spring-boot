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

import lombok.val;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

public abstract class AbstractChaosMonkeyAdvice implements MethodInterceptor {

    @Override
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        // this cast should always succeed within spring
        val pjp = new MethodInvocationProceedingJoinPoint((ProxyMethodInvocation) invocation);
        return invoke(pjp);
    }

    protected abstract Object invoke(ProceedingJoinPoint pjp) throws Throwable;

    protected String calculatePointcut(String target) {
        return target.replaceAll("\\(\\)", "").replaceAll("\\)", "").replaceAll("\\(", ".");
    }

    protected String createSignature(MethodSignature signature) {
        return signature.getDeclaringTypeName() + "." + signature.getMethod().getName();
    }
}
