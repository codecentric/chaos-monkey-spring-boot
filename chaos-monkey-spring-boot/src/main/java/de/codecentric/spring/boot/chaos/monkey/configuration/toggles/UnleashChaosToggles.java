package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import io.getunleash.Unleash;

public class UnleashChaosToggles implements ChaosToggles {
  private final Unleash unleash;

  public UnleashChaosToggles(Unleash unleash) {
    this.unleash = unleash;
  }

  @Override
  public boolean isEnabled(String toggleName) {
    return unleash.isEnabled(toggleName);
  }
}
