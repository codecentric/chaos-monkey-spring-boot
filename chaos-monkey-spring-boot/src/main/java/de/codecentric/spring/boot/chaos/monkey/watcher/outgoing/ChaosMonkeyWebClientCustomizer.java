package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.web.reactive.function.client.WebClient.Builder;

public class ChaosMonkeyWebClientCustomizer implements WebClientCustomizer {

  private final ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher;

  public ChaosMonkeyWebClientCustomizer(ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher) {
    this.chaosMonkeyWebClientWatcher = chaosMonkeyWebClientWatcher;
  }

  @Override
  public void customize(Builder webClientBuilder) {
    webClientBuilder.filter(chaosMonkeyWebClientWatcher);
  }
}
