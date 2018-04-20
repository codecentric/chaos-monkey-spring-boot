package de.codecentric.spring.boot.chaos.monkey.endpoints;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;

/**
 * @author Benjamin Wilms
 */
@ConfigurationProperties(prefix = "endpoints.chaosmonkey")
public class ChaosMonkeyEndpoint extends AbstractEndpoint<ChaosMonkeySettings> {

    private ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        super("chaosmonkey");
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @Override
    public ChaosMonkeySettings invoke() {
        return chaosMonkeySettings;
    }
}
