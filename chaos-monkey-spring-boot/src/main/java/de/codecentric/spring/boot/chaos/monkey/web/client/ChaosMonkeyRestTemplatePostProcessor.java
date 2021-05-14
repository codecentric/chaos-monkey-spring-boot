package de.codecentric.spring.boot.chaos.monkey.web.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

public class ChaosMonkeyRestTemplatePostProcessor implements BeanPostProcessor {

  private final ChaosMonkeyRestTemplateInterceptor interceptor;

  public ChaosMonkeyRestTemplatePostProcessor(
      final ChaosMonkeyRestTemplateInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public Object postProcessBeforeInitialization(
      final Object bean,
      final String beanName) throws BeansException {

    if (bean instanceof RestTemplate) {
      //inject interceptors
      ((RestTemplate) bean).getInterceptors().add(interceptor);
    }
    return bean;
  }
}
