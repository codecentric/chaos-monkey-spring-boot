package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

/**
 * @author Marcel Becker
 */
public class ChaosMonkeyRestTemplatePostProcessor implements BeanPostProcessor {

  private final ChaosMonkeyRestTemplateWatcher interceptor;

  public ChaosMonkeyRestTemplatePostProcessor(final ChaosMonkeyRestTemplateWatcher interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {

    if (bean instanceof RestTemplate) {
      ((RestTemplate) bean).getInterceptors().add(interceptor);
    }
    return bean;
  }
}
