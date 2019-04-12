package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Chaos Monkey for all Runtime scoped attacks.
 *
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRuntimeScope {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyRuntimeScope.class);

    private final ChaosMonkeySettings chaosMonkeySettings;
    private final List<ChaosMonkeyRuntimeAssault> assaults;

    public ChaosMonkeyRuntimeScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyRuntimeAssault> assaults) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = assaults;
    }

    public void callChaosMonkey() {
        if (isEnabled()) {
            LOGGER.info("Executing all runtime-scoped attacks");
            chooseAndRunAttacks();
        }

    }

    private void chooseAndRunAttacks() {
        assaults.stream()
                .filter(ChaosMonkeyAssault::isActive)
                .forEach(ChaosMonkeyAssault::attack);
    }


    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }
}
