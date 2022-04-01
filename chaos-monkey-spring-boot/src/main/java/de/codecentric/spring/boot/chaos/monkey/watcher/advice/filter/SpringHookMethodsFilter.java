package de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.support.RequestHandledEvent;

public class SpringHookMethodsFilter extends StaticMethodMatcher {

  @Override
  public boolean matches(Method method, Class<?> targetClass) {
    String name = method.getName();
    return !name.matches("postProcess.*Initialization")
        && (!name.equals("onApplicationEvent")
            || Arrays.equals(method.getParameterTypes(), new Object[] {RequestHandledEvent.class}))
        && !FactoryBean.class.isAssignableFrom(targetClass);
  }

  public static SpringHookMethodsFilter INSTANCE = new SpringHookMethodsFilter();
}
