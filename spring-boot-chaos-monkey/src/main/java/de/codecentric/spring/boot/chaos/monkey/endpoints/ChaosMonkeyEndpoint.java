package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkeyEndpoint extends AbstractEndpoint<ChaosMonkeySettings> {

    private ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        super("chaosmonkey");
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    public ChaosMonkeySettings invoke() {
        return this.chaosMonkeySettings;
    }
}
