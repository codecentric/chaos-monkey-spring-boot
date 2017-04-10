package de.mrbw.chaos.monkey.configuration;

import de.mrbw.chaos.monkey.component.ChaosMonkey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Benjamin Wilms
 */
@Configuration
public class ChaosMonkeyConfiguration {

    @Bean
    public ChaosMonkey chaosMonkey() {
        return new ChaosMonkey();
    }
}
