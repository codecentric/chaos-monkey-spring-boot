package de.codecentric.spring.boot.chaos.monkey.watcher.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {
      "chaos.monkey.enabled=true",
      "chaos.monkey.watcher.actuator-health=true",
      "chaos.monkey.assaults.exceptions-active=true"
    },
    classes = {ChaosDemoApplication.class})
@ActiveProfiles("chaos-monkey")
class SpringBootHealthIndicatorAspectIntegrationTest {

  @Autowired private List<HealthIndicator> healthIndicators;

  @Test
  public void testIndicatorsAreDown() {

    this.healthIndicators.forEach(
        healthIndicator -> {
          assertThat(healthIndicator.getHealth(Boolean.TRUE).getStatus())
              .isEqualTo(Health.down().build().getStatus());
        });
  }
}
