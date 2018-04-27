package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Benjamin Wilms
 */
@Configuration
public class EndpointConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointConfiguration.class);
    private final ChaosMonkeySettings chaosMonkeySettings;

    public EndpointConfiguration(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        LOGGER.info("EndpointAutoConfiguration active");
    }

    @ConditionalOnEnabledEndpoint
    @Bean
    public ChaosMonkeyEndpoint assaultEndpoint() {
        return new ChaosMonkeyEndpoint(chaosMonkeySettings);
    }
}
