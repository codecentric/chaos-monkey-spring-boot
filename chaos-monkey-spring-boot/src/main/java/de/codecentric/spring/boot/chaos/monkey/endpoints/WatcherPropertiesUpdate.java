package de.codecentric.spring.boot.chaos.monkey.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import java.util.function.Consumer;

@Data @NoArgsConstructor @Validated @JsonInclude(JsonInclude.Include.NON_NULL)
public class WatcherPropertiesUpdate {

    @Nullable
    private Boolean controller;

    @Nullable
    private Boolean restController;

    @Nullable
    private Boolean service;

    @Nullable
    private Boolean repository;

    @Nullable
    private Boolean component;

    private <T> void applyTo(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    @SuppressWarnings("all")
    public void applyTo(WatcherProperties t) {
        applyTo(controller, t::setController);
        applyTo(restController, t::setRestController);
        applyTo(service, t::setService);
        applyTo(repository, t::setRepository);
        applyTo(component, t::setComponent);
    }
}
