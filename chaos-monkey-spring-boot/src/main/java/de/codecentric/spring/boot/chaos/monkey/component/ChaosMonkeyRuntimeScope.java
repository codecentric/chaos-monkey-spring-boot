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
package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chaos Monkey for all Runtime scoped attacks.
 *
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRuntimeScope {

    private static final Logger Logger = LoggerFactory.getLogger(ChaosMonkeyRuntimeScope.class);

    private final ChaosMonkeySettings chaosMonkeySettings;

    private final List<ChaosMonkeyRuntimeAssault> assaults;

    public ChaosMonkeyRuntimeScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyRuntimeAssault> assaults) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = assaults;
    }

    public void callChaosMonkey() {
        if (isEnabled()) {
            Logger.info("Executing all runtime-scoped attacks");
            chooseAndRunAttacks();
        }
    }

    private void chooseAndRunAttacks() {
        assaults.stream().filter(ChaosMonkeyAssault::isActive).forEach(ChaosMonkeyAssault::attack);
    }

    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }
}
