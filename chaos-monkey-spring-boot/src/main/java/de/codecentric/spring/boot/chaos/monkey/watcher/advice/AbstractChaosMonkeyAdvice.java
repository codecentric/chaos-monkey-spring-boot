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
