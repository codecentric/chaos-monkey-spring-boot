package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.ChaosMonkeyStatusResponseDto;
import java.time.Duration;

public class BaseChaosMonkeyEndpoint {

  protected final ChaosMonkeySettings chaosMonkeySettings;

  public BaseChaosMonkeyEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
    this.chaosMonkeySettings = chaosMonkeySettings;
  }

  public AssaultPropertiesUpdate getAssaultProperties() {
    return this.chaosMonkeySettings.getAssaultProperties().toDto();
  }

  public ChaosMonkeyStatusResponseDto enableChaosMonkey() {
    return setChaosMonkeyEnabled(true);
  }

  public ChaosMonkeyStatusResponseDto disableChaosMonkey() {
    return setChaosMonkeyEnabled(false);
  }

  private ChaosMonkeyStatusResponseDto setChaosMonkeyEnabled(boolean enabled) {
    ChaosMonkeyProperties chaosMonkeyProperties =
        this.chaosMonkeySettings.getChaosMonkeyProperties();
    Long lastTimestamp = chaosMonkeyProperties.getLastEnabledToggleTimestamp();
    boolean wasEnabled = chaosMonkeyProperties.isEnabled();

    chaosMonkeyProperties.setEnabled(enabled);

    Duration enabledFor =
        wasEnabled && lastTimestamp != null
            ? Duration.ofMillis(System.currentTimeMillis() - lastTimestamp)
            : null;
    Long timestamp = chaosMonkeyProperties.getLastEnabledToggleTimestamp();

    return new ChaosMonkeyStatusResponseDto(enabled, timestamp, enabledFor);
  }

  public ChaosMonkeyStatusResponseDto getStatus() {
    ChaosMonkeyProperties chaosMonkeyProperties =
        this.chaosMonkeySettings.getChaosMonkeyProperties();
    Long lastEnabledToggleTime = chaosMonkeyProperties.getLastEnabledToggleTimestamp();
    boolean enabled = chaosMonkeyProperties.isEnabled();
    Duration enabledFor =
        enabled && lastEnabledToggleTime != null
            ? Duration.ofMillis(System.currentTimeMillis() - lastEnabledToggleTime)
            : null;
    return new ChaosMonkeyStatusResponseDto(enabled, lastEnabledToggleTime, enabledFor);
  }

  public WatcherProperties getWatcherProperties() {
    return this.chaosMonkeySettings.getWatcherProperties();
  }
}
