package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyEndpoint;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * @author Benjamin Wilms
 */
@Configuration
@Profile("chaos-monkey")
@AutoConfigureAfter(ChaosMonkeyConfiguration.class)
@ConditionalOnBean(EndpointAutoConfiguration.class)
public class EndpointConfiguration {
    private final ChaosMonkeySettings chaosMonkeySettings;

    public EndpointConfiguration(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @Bean
    @ConditionalOnEnabledEndpoint(value = "chaosmonkey")
    public ChaosMonkeyEndpoint assaultEndpoint() {
        return new ChaosMonkeyEndpoint(chaosMonkeySettings);
    }
}

