package de.codecentric.spring.boot.chaos.monkey.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ChaosMonkeyConditionTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(UserConfigurations.of(ChaosMonkeyConfiguration.class));

  @Test
  public void chaosmonkeyShouldBeUnload() {
    runner.run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isFalse());
  }

  @Test
  public void chaosmonkeyShouldBeloadedWithProfile() {
    runner
        .withSystemProperties("spring.profiles.active=chaos-monkey")
        .run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isTrue());
  }

  @Test
  public void chaosmonkeyShouldBeloadedWithProperty() {
    runner
        .withSystemProperties("LOAD_CHAOS_MONKEY=true")
        .run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isTrue());
  }
}
