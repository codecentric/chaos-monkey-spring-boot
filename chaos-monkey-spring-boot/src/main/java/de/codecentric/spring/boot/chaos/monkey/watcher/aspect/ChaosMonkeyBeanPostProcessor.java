package de.codecentric.spring.boot.chaos.monkey.watcher.aspect;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.SingletonMetadataAwareAspectInstanceFactory;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;

@Slf4j
public class ChaosMonkeyBeanPostProcessor extends AbstractAdvisingBeanPostProcessor {

  private final WatcherProperties watcherProperties;
  private final Map<Object, String> activeBeanNameCache = new WeakHashMap<>();

  public ChaosMonkeyBeanPostProcessor(
      WatcherProperties watcherProperties,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    this(watcherProperties, requestScope, eventPublisher, new ReflectiveAspectJAdvisorFactory());
  }

  public ChaosMonkeyBeanPostProcessor(
      WatcherProperties watcherProperties,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher,
      AspectJAdvisorFactory advisorFactory) {
    this.watcherProperties = watcherProperties;
    SpringBeanAspect aspect = new SpringBeanAspect(requestScope, eventPublisher);
    this.advisor = convertAspectToAdvisor(advisorFactory, aspect, "springBeanAspect");
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

  private Advisor convertAspectToAdvisor(
      AspectJAdvisorFactory advisorFactory, Object aspect, String name) {
    val factory = new SingletonMetadataAwareAspectInstanceFactory(aspect, name);
    List<Advisor> classAdvisors = advisorFactory.getAdvisors(factory);
    if (classAdvisors.size() != 1) {
      throw new IllegalArgumentException(name + " must contain exactly one advisor");
    }
    return classAdvisors.get(0);
  }

  @Aspect
  @AllArgsConstructor
  public class SpringBeanAspect extends ChaosMonkeyBaseAspect {

    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    private MetricEventPublisher metricEventPublisher;

    @Around("allPublicMethodPointcut()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
      if (watcherProperties.getBeans().contains(activeBeanNameCache.get(pjp.getThis()))) {
        log.debug("Watching public method on bean class: {}", pjp.getSignature());

        if (metricEventPublisher != null) {
          metricEventPublisher.publishMetricEvent(
              calculatePointcut(pjp.toShortString()), MetricType.BEAN);
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        chaosMonkeyRequestScope.callChaosMonkey(ChaosTarget.BEAN, createSignature(signature));
      }
      return pjp.proceed();
    }
  }
}
