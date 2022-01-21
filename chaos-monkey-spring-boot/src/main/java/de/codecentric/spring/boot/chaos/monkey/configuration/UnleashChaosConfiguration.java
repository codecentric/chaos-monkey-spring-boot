package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.UnleashChaosToggles;
import io.getunleash.Unleash;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Unleash.class)
@ConditionalOnProperty(value = "chaos.monkey.toggle.unleash.enabled")
public class UnleashChaosConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(Unleash.class)
  public ChaosToggles unleashChaosToggles(Unleash unleash) {
    return new UnleashChaosToggles(unleash);
  }
}
