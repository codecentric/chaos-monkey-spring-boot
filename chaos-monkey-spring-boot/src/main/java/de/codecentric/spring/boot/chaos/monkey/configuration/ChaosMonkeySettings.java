package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Benjamin Wilms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChaosMonkeySettings {

    @NotNull
    private ChaosMonkeyProperties chaosMonkeyProperties;
    @NotNull
    private AssaultProperties assaultProperties;
    @NotNull
    private WatcherProperties watcherProperties;

}
