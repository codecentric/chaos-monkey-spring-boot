package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;

public class DefaultChaosToggleNameMapper implements ChaosToggleNameMapper {
  protected final String togglePrefix;

  public DefaultChaosToggleNameMapper(String togglePrefix) {
    this.togglePrefix = togglePrefix;
  }

  @Override
  public String mapName(ChaosTarget type, String name) {
    if (type == null) {
      return togglePrefix + ".unknown";
    }

    return togglePrefix + "." + type.getName();
  }
}
