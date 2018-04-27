package de.codecentric.spring.boot.chaos.monkey.controller;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChaosMonkeyController {

    private final ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyController(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @PostMapping("/chaos-monkey/setting")
    public void updateSettings(@RequestBody ChaosMonkeyProperties chaosMonkeyProperties) {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(chaosMonkeyProperties.isEnabled());
    }
}
