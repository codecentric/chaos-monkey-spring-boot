package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.List;
import java.util.function.Consumer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatcherPropertiesUpdate {

  @Nullable private Boolean controller;

  @Nullable private Boolean restController;

  @Nullable private Boolean service;

  @Nullable private Boolean repository;

  @Nullable private Boolean component;

  @Nullable private Boolean restTemplate;

  @Nullable private Boolean webClient;

  @Nullable private Boolean actuatorHealth;

  @Nullable private List<String> beans;

  private <T> void applyTo(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  public void applyTo(WatcherProperties t) {
    applyTo(controller, t::setController);
    applyTo(restController, t::setRestController);
    applyTo(service, t::setService);
    applyTo(repository, t::setRepository);
    applyTo(component, t::setComponent);
    applyTo(restTemplate, t::setRestTemplate);
    applyTo(webClient, t::setWebClient);
    applyTo(actuatorHealth, t::setActuatorHealth);
    applyTo(beans, t::setBeans);
  }
}
