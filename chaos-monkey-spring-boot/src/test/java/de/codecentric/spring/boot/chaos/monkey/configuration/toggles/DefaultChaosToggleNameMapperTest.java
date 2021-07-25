package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import static org.junit.jupiter.api.Assertions.*;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultChaosToggleNameMapperTest {

  private DefaultChaosToggleNameMapper sut;

  @BeforeEach
  public void setup() {
    sut = new DefaultChaosToggleNameMapper("toggle.prefix");
  }

  @Test
  public void chaosTypeCanBeNull() {
    assertEquals(sut.mapName(null, "com.example.MyController.hello"), "toggle.prefix.unknown");
  }

  @Test
  public void chaosTypeNameIsUsedAsSuffix() {
    assertEquals(
        sut.mapName(ChaosTarget.REPOSITORY, "com.example.MyController.hello"),
        "toggle.prefix.repository");
  }
}
