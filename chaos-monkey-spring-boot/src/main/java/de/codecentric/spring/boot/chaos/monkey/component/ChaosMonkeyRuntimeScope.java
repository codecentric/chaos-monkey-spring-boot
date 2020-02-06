package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chaos Monkey for all Runtime scoped attacks.
 *
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRuntimeScope {

  private static final Logger Logger = LoggerFactory.getLogger(ChaosMonkeyRuntimeScope.class);

  private final ChaosMonkeySettings chaosMonkeySettings;

  private final List<ChaosMonkeyRuntimeAssault> assaults;

  public ChaosMonkeyRuntimeScope(
      ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyRuntimeAssault> assaults) {
    this.chaosMonkeySettings = chaosMonkeySettings;
    this.assaults = assaults;
  }

  public void callChaosMonkey() {
    if (isEnabled()) {
      Logger.info("Executing all runtime-scoped attacks");
      chooseAndRunAttacks();
    }
  }

  private void chooseAndRunAttacks() {
    assaults.stream().filter(ChaosMonkeyAssault::isActive).forEach(ChaosMonkeyAssault::attack);
  }

  private boolean isEnabled() {
    return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
  }
}
