package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultChaosTogglesTest {

  private DefaultChaosToggles sut;

  @BeforeEach
  public void setup() {
    sut = new DefaultChaosToggles();
  }

  @Test
  public void defaultToggleShouldBeEnabledAlways() {
    assertTrue(sut.isEnabled("chaos.monkey.repository"));
    assertTrue(sut.isEnabled(""));
  }
}
