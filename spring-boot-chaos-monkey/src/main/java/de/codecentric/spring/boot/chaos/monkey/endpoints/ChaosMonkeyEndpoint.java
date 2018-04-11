package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * @author Benjamin Wilms
 */
@Endpoint(id = "chaosmonkey",enableByDefault = false)
public class ChaosMonkeyEndpoint {

    private ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @ReadOperation
    public ChaosMonkeySettings chaosMonkeySettings() {
        return chaosMonkeySettings;
    }

}
