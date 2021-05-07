package de.codecentric.spring.boot.chaos.monkey.component;

public enum ChaosTarget {
  CONTROLLER("controller"),
  REST_CONTROLLER("restController"),
  REPOSITORY("repository"),
  COMPONENT("component"),
  SERVICE("service");

  private final String name;

  ChaosTarget(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
