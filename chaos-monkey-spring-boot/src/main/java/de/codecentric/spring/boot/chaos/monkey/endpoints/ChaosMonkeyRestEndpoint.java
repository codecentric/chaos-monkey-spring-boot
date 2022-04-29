/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRuntimeScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyScheduler;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.ChaosMonkeySettingsDto;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.ChaosMonkeyStatusResponseDto;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.WatcherPropertiesUpdate;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RestControllerEndpoint(enableByDefault = false, id = "chaosmonkey")
public class ChaosMonkeyRestEndpoint extends BaseChaosMonkeyEndpoint {

    private final ChaosMonkeyRuntimeScope runtimeScope;

    private final ChaosMonkeyScheduler scheduler;

    public ChaosMonkeyRestEndpoint(ChaosMonkeySettings chaosMonkeySettings, ChaosMonkeyRuntimeScope runtimeScope, ChaosMonkeyScheduler scheduler) {
        super(chaosMonkeySettings);
        this.runtimeScope = runtimeScope;
        this.scheduler = scheduler;
    }

    @PostMapping("/assaults")
    @ResponseBody
    public String updateAssaultProperties(@RequestBody @Validated AssaultPropertiesUpdate assaultProperties) {
        assaultProperties.applyTo(chaosMonkeySettings.getAssaultProperties());
        scheduler.reloadConfig();
        return "Assault config has changed";
    }

    @PostMapping("/assaults/runtime/attack")
    @ResponseBody
    public String attack() {
        runtimeScope.callChaosMonkey();
        return "Started runtime assaults";
    }

    @GetMapping("/assaults")
    @Override
    public AssaultPropertiesUpdate getAssaultProperties() {
        return super.getAssaultProperties();
    }

    @PostMapping("/enable")
    @Override
    public ChaosMonkeyStatusResponseDto enableChaosMonkey() {
        return super.enableChaosMonkey();
    }

    @PostMapping("/disable")
    @Override
    public ChaosMonkeyStatusResponseDto disableChaosMonkey() {
        return super.disableChaosMonkey();
    }

    @GetMapping
    public ChaosMonkeySettingsDto status() {
        return this.chaosMonkeySettings.toDto();
    }

    @GetMapping("/status")
    @Override
    public ChaosMonkeyStatusResponseDto getStatus() {
        return super.getStatus();
    }

    @PostMapping("/watchers")
    @ResponseBody
    public String updateWatcherProperties(@RequestBody @Validated WatcherPropertiesUpdate watcherProperties) {
        watcherProperties.applyTo(chaosMonkeySettings.getWatcherProperties());
        scheduler.reloadConfig();

        return "Watcher config has changed";
    }

    @GetMapping("/watchers")
    @Override
    public WatcherProperties getWatcherProperties() {
        return super.getWatcherProperties();
    }
}
