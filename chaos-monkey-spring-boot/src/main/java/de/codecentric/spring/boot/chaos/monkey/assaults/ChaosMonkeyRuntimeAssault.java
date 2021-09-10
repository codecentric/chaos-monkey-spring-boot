package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;

public interface ChaosMonkeyRuntimeAssault extends ChaosMonkeyAssault {
  String getCronExpression(AssaultProperties assaultProperties);
}
