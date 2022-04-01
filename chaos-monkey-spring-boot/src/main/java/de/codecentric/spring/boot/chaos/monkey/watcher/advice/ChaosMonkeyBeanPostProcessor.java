package de.codecentric.spring.boot.chaos.monkey.watcher.advice;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.SpringHookMethodsFilter;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

@Slf4j
public class ChaosMonkeyBeanPostProcessor extends AbstractAdvisingBeanPostProcessor {

  private final WatcherProperties watcherProperties;
  private final Map<Object, String> activeBeanNameCache = new WeakHashMap<>();

  public ChaosMonkeyBeanPostProcessor(
      WatcherProperties watcherProperties,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    this.watcherProperties = watcherProperties;
    val advice =
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.BEAN) {
          @Override
          public boolean isEnabled(ProceedingJoinPoint pjp) {
            return watcherProperties.getBeans().contains(activeBeanNameCache.get(pjp.getThis()));
          }
        };
    this.advisor =
        new DefaultPointcutAdvisor(
            new ComposablePointcut(SpringHookMethodsFilter.INSTANCE), advice);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (watcherProperties.getBeans().contains(beanName)) {
      Object proxy = super.postProcessAfterInitialization(bean, beanName);
      activeBeanNameCache.put(proxy, beanName);
      return proxy;
    }
    return bean;
  }
}
