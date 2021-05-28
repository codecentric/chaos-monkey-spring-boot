package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.reactive.function.client.WebClient;

/** @author Marcel Becker */
public class ChaosMonkeyWebClientPostProcessor implements BeanPostProcessor {

  private final ChaosMonkeyWebClientWatcher filter;

  public ChaosMonkeyWebClientPostProcessor(final ChaosMonkeyWebClientWatcher filter) {
    this.filter = filter;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {
    final Object target;
    if (bean instanceof WebClient) {
      // create a copy of WebClient whose settings are replicated from the current WebClient.
      target = ((WebClient) bean).mutate().filter(filter).build();
    } else {
      target = bean;
    }
    return target;
  }
}
