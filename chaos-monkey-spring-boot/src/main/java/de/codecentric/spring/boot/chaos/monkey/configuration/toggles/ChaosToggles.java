package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

public interface ChaosToggles {
  boolean isEnabled(String toggleName);
}
