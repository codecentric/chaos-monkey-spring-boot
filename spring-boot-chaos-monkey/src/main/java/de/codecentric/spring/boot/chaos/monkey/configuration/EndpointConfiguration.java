package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Benjamin Wilms
 */
@Configuration
@ConditionalOnClass(AbstractEndpoint.class)
@ConditionalOnProperty(prefix = "endpoints.chaosmonkey", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EndpointConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointConfiguration.class);
    private final ChaosMonkeySettings chaosMonkeySettings;

    public EndpointConfiguration(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        LOGGER.info("EndpointAutoConfiguration active");
    }

    @Bean
    public ChaosMonkeyEndpoint assaultEndpoint() {
        return new ChaosMonkeyEndpoint(chaosMonkeySettings);
    }
}
