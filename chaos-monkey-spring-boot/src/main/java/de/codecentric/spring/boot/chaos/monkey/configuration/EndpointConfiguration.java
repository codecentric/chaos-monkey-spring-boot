package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
        LOGGER.debug("EndpointAutoConfiguration active");
    }

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "endpoints.chaosmonkey", name = "enabled", matchIfMissing = false)
    @Bean
    public ChaosMonkeyController assaultEndpoint() {
        return new ChaosMonkeyController(chaosMonkeySettings);
    }
}
