package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChaosTarget {
  CONTROLLER("controller", MetricType.CONTROLLER, WatcherProperties::isController),
  REST_CONTROLLER("restController", MetricType.RESTCONTROLLER, WatcherProperties::isRestController),
  REPOSITORY("repository", MetricType.REPOSITORY, WatcherProperties::isRepository),
  COMPONENT("component", MetricType.COMPONENT, WatcherProperties::isComponent),
  SERVICE("service", MetricType.SERVICE, WatcherProperties::isService),
  REST_TEMPLATE("restTemplate", null, WatcherProperties::isRestTemplate),
  WEB_CLIENT("webClient", null, WatcherProperties::isWebClient),
  ACTUATOR_HEALTH("actuatorHealth", null, WatcherProperties::isActuatorHealth),
  BEAN("bean", MetricType.BEAN, watcherProperties -> true);

  @Getter private final String name;
  @Getter private final MetricType metricType;

  private final Predicate<WatcherProperties> isEnabled;

  public boolean isEnabled(WatcherProperties watcherProperties) {
    return isEnabled.test(watcherProperties);
  }
}
