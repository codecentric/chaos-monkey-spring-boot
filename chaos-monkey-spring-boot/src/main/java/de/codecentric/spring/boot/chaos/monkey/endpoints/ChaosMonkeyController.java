package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chaosmonkey")
public class ChaosMonkeyController implements MvcEndpoint {

    private ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyController(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @PostMapping("/configuration/assaults")
    public ResponseEntity<String> updateAssaultProperties(@RequestBody @Validated AssaultProperties assaultProperties) {

        this.chaosMonkeySettings.setAssaultProperties(assaultProperties);
        return ResponseEntity.ok().body("Assault config has changed");
    }

    @PostMapping("/enable")
    public ResponseEntity<String> enableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);
        return ResponseEntity.ok().body("Chaos Monkey is enabled");
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);
        return ResponseEntity.ok().body("Chaos Monkey is disabled");
    }


    @GetMapping("/status")
    public ResponseEntity<String> getChaosMonkeyStatus() {
        if (this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled())
            return ResponseEntity.status(HttpStatus.OK).body("Ready to be evil!");
        else
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("You switched me off!");
    }

    @GetMapping("/configuration")
    public ChaosMonkeySettings getSettings() {
        return this.chaosMonkeySettings;
    }

    @GetMapping("/configuration/assaults")
    public AssaultProperties getAssaultSettings() {
        return this.chaosMonkeySettings.getAssaultProperties();
    }

    /***
     * Watcher can only be viewed, not changed at runtime. They are initialized at Application start.
     * @return
     */
    @GetMapping("/configuration/watcher")
    public WatcherProperties getWatcherSettings() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }


}
