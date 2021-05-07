package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;

public interface ChaosToggleNameMapper {
  String mapName(ChaosTarget type, String name);
}
