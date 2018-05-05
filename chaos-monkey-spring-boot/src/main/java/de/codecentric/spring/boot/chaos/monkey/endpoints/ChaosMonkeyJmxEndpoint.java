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
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.jmx.annotation.JmxEndpoint;

/**
 * @author Benjamin Wilms
 */
@JmxEndpoint(enableByDefault = false, id = "chaosmonkeyjmx")
public class ChaosMonkeyJmxEndpoint {

    private ChaosMonkeySettings chaosMonkeySettings;

    public ChaosMonkeyJmxEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @ReadOperation
    public AssaultProperties getAssaultProperties() {
        return chaosMonkeySettings.getAssaultProperties();
    }

    @WriteOperation
    public String toggleLatencyAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setLatencyActive(!this.getAssaultProperties().isLatencyActive());
        return String.valueOf(this.getAssaultProperties().isLatencyActive());
    }

    @WriteOperation
    public String toggleExceptionAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setExceptionsActive(!this.getAssaultProperties().isExceptionsActive());
        return String.valueOf(this.getAssaultProperties().isExceptionsActive());
    }

    @WriteOperation
    public String toggleKillApplicationAssault() {
        this.chaosMonkeySettings.getAssaultProperties().setKillApplicationActive(!this.getAssaultProperties().isKillApplicationActive());
        return String.valueOf(this.getAssaultProperties().isKillApplicationActive());
    }

    @ReadOperation()
    public String isChaosMonkeyActive() {
        return String.valueOf(this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled());
    }

    @WriteOperation
    public String enableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);
        return "Chaos Monkey is enabled";
    }

    @WriteOperation
    public String disableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);
        return "Chaos Monkey is disabled";
    }

    @ReadOperation
    public WatcherProperties getWatcherProperties() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }


}
