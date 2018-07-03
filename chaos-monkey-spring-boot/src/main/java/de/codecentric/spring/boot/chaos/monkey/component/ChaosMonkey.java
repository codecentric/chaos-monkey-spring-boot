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

package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkey {

    private ChaosMonkeySettings chaosMonkeySettings;

    private LatencyAssault latencyAssault;
    private ExceptionAssault exceptionAssault;
    private KillAppAssault killAppAssault;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);


    public ChaosMonkey(ChaosMonkeySettings chaosMonkeySettings, LatencyAssault assault, ExceptionAssault exceptionAssault, KillAppAssault killAppAssault) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.latencyAssault = assault;
        this.exceptionAssault = exceptionAssault;
        this.killAppAssault = killAppAssault;
    }

    public void callChaosMonkey() {
        if (isTrouble() && isEnabled()) {
            // TODO: Refactoring to Assault Management!
            int exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(3);

            if (allAssaultsActive()) {

                switch (exceptionRand) {
                    case 1:
                        latencyAssault.attack();
                        break;
                    case 2:
                        exceptionAssault.attack();
                        break;
                    case 3:
                        killAppAssault.attack();
                        break;
                }
            } else if (isLatencyAndExceptionActive()) {
                exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(2);
                switch (exceptionRand) {
                    case 1:
                        latencyAssault.attack();
                        break;
                    case 2:
                        exceptionAssault.attack();
                        break;
                }

            } else if (isLatencyAndKillAppActive()) {
                exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(2);
                switch (exceptionRand) {
                    case 1:
                        latencyAssault.attack();
                        break;
                    case 2:
                        killAppAssault.attack();
                        break;
                }

            } else if (isExceptionAndKillAppActive()) {
                exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(2);
                switch (exceptionRand) {
                    case 1:
                        exceptionAssault.attack();
                        break;
                    case 2:
                        killAppAssault.attack();
                        break;
                }

            } else if (latencyAssault.isActive()) {
                latencyAssault.attack();
            } else if (exceptionAssault.isActive()) {
                exceptionAssault.attack();
            } else if (killAppAssault.isActive()) {
                killAppAssault.attack();
            }
        }

    }

    private boolean isLatencyAndKillAppActive() {
        return latencyAssault.isActive() && !exceptionAssault.isActive() &&
                killAppAssault.isActive();
    }

    private boolean isExceptionAndKillAppActive() {
        return !latencyAssault.isActive() && exceptionAssault.isActive() &&
                killAppAssault.isActive();
    }

    private boolean isLatencyAndExceptionActive() {
        return latencyAssault.isActive() && exceptionAssault.isActive() &&
                !killAppAssault.isActive();
    }


    private boolean allAssaultsActive() {
        return latencyAssault.isActive() && exceptionAssault.isActive() && killAppAssault.isActive();
    }

    private boolean isTrouble() {
        return chaosMonkeySettings.getAssaultProperties().getTroubleRandom() >= chaosMonkeySettings.getAssaultProperties().getLevel();
    }

    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }
}