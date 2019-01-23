/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestControllerEndpoint(enableByDefault = false, id = "chaosmonkey")
public class ChaosMonkeyRestEndpoint {

    private final ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyRestEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }


    @PostMapping("/assaults")
    public ResponseEntity<String> updateAssaultProperties(@RequestBody @Validated AssaultProperties assaultProperties) {
        this.chaosMonkeySettings.setAssaultProperties(assaultProperties);
        return ResponseEntity.ok().body("Assault config has changed");
    }

    @GetMapping("/assaults")
    public AssaultProperties getAssaultSettings() {
        return this.chaosMonkeySettings.getAssaultProperties();
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

    @GetMapping
    public ChaosMonkeySettings status() {
        return this.chaosMonkeySettings;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        if (this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled())
            return ResponseEntity.status(HttpStatus.OK).body("Ready to be evil!");
        else
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("You switched me off!");
    }

    /***
     * Watcher can only be viewed, not changed at runtime. They are initialized at Application start.
     * @return watch settings
     */
    @GetMapping("/watcher")
    public WatcherProperties getWatcherSettings() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }


}
