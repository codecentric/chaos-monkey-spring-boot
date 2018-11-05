package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.AssaultType;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Chaos Monkey for all Runtime scoped attacks
 *
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRuntimeScope {

    private final ChaosMonkeySettings chaosMonkeySettings;
    private final List<ChaosMonkeyAssault> assaults;

    public ChaosMonkeyRuntimeScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyAssault> assaults) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = assaults;
    }

    public void callChaosMonkey() {
        if (isEnabled())
            chooseAndRunAttack();

    }

    private void chooseAndRunAttack() {
        List<ChaosMonkeyAssault> activeAssaults = assaults.stream()
                .filter(chaosMonkeyAssault -> chaosMonkeyAssault.getAssaultType() == AssaultType.RUNTIME)
                .filter(ChaosMonkeyAssault::isActive)
                .collect(Collectors.toList());
        if (isEmpty(activeAssaults)) {
            return;
        }
        getRandomFrom(activeAssaults).attack();

    }

    private ChaosMonkeyAssault getRandomFrom(List<ChaosMonkeyAssault> activeAssaults) {
        int exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(activeAssaults.size());
        return activeAssaults.get(exceptionRand);
    }

    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }
}
