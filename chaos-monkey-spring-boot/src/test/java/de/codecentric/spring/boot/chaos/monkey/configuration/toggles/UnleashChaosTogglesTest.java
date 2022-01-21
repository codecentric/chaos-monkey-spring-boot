package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import static org.junit.jupiter.api.Assertions.*;

import io.getunleash.FakeUnleash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnleashChaosTogglesTest {
  private UnleashChaosToggles sut;
  private FakeUnleash fakeUnleash;

  @BeforeEach
  public void setup() {
    fakeUnleash = new FakeUnleash();
    sut = new UnleashChaosToggles(fakeUnleash);
  }

  @Test
  public void unleashTogglesAreDisabledByDefault() {
    assertFalse(sut.isEnabled("chaos.monkey.repository"));
  }

  @Test
  public void unleashTogglesThatAreEnabledAlsoEnableTheChaosToggle() {
    fakeUnleash.enable("chaos.monkey.repository");
    assertTrue(sut.isEnabled("chaos.monkey.repository"));
  }

  @Test
  public void unleashTogglesThatAreEnabledThatDontMatchTheChaosToggleAUnaffected() {
    fakeUnleash.enable("chaos.monkey.controller");
    assertFalse(sut.isEnabled("chaos.monkey.repository"));
  }
}
