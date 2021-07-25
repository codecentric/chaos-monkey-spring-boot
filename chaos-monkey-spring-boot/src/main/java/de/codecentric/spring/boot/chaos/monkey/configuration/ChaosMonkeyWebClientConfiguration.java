package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientCustomizer;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientPostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientWatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "chaos.monkey.watcher", value = "web-client", havingValue = "true")
@ConditionalOnClass(value = WebClient.class)
class ChaosMonkeyWebClientConfiguration {

  @Bean
  public ChaosMonkeyWebClientPostProcessor chaosMonkeyWebClientPostProcessor(
      final ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher) {
    return new ChaosMonkeyWebClientPostProcessor(chaosMonkeyWebClientWatcher);
  }

  @Bean
  public ChaosMonkeyWebClientCustomizer chaosMonkeyWebClientCustomizer(
      final ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher) {
    return new ChaosMonkeyWebClientCustomizer(chaosMonkeyWebClientWatcher);
  }

  @Bean
  @DependsOn("chaosMonkeyRequestScope")
  public ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher(
      final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
      final WatcherProperties watcherProperties,
      final AssaultProperties assaultProperties) {
    return new ChaosMonkeyWebClientWatcher(
        chaosMonkeyRequestScope, watcherProperties, assaultProperties);
  }
}
