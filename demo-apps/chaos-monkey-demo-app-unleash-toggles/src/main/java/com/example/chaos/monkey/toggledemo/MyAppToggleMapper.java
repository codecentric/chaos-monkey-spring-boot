package com.example.chaos.monkey.toggledemo;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;

public class MyAppToggleMapper extends DefaultChaosToggleNameMapper {
  public MyAppToggleMapper(String prefix) {
    super(prefix);
  }

  @Override
  public String mapName(ChaosTarget type, String name) {
    if (type.equals(ChaosTarget.CONTROLLER) && name.toLowerCase().contains("hello")) {
      return this.togglePrefix + ".howdy";
    }
    return super.mapName(type, name);
  }
}
