package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

public class DefaultChaosToggles implements ChaosToggles {
  @Override
  public boolean isEnabled(String toggleName) {
    return true;
  }
}
