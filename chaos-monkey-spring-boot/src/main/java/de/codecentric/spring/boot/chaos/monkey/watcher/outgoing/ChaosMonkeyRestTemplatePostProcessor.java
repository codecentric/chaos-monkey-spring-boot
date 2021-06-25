package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

/** @author Marcel Becker */
public class ChaosMonkeyRestTemplatePostProcessor implements BeanPostProcessor {

  private final ChaosMonkeyRestTemplateCustomizer restTemplateCustomizer;

  public ChaosMonkeyRestTemplatePostProcessor(
      ChaosMonkeyRestTemplateCustomizer restTemplateCustomizer) {
    this.restTemplateCustomizer = restTemplateCustomizer;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {
    if (bean instanceof RestTemplate) {
      this.restTemplateCustomizer.customize((RestTemplate) bean);
    }
    return bean;
  }
}
