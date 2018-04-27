package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey")
public class ChaosMonkeyProperties {

    @Value("${enabled:false}")
    private boolean enabled;

}
