package de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.StaticMethodMatcher;

@RequiredArgsConstructor
public class MethodNameFilter extends StaticMethodMatcher {

  private final String methodName;

  @Override
  public boolean matches(Method method, Class<?> targetClass) {
    return methodName.equals(method.getName());
  }
}
