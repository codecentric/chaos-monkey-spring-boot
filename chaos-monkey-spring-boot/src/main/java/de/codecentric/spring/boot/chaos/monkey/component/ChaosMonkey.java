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

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Benjamin Wilms
 */
@Component
public class ChaosMonkey {

    private ChaosMonkeySettings chaosMonkeySettings;

    @Autowired
    private ApplicationContext context;


    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);


    public ChaosMonkey(ChaosMonkeySettings chaosMonkeySettings) {
        this.chaosMonkeySettings = chaosMonkeySettings;
    }


    public void callChaosMonkey() {
        if (isTrouble() && isEnabled()) {
            int exceptionRand = chaosMonkeySettings.getAssaultProperties().getExceptionRandom();

            if (chaosMonkeySettings.getAssaultProperties().isLatencyActive() && chaosMonkeySettings.getAssaultProperties().isExceptionsActive()) {
                // Timeout or Exception?
                if (exceptionRand < 7) {
                    generateLatency();
                } else {
                    generateChaosException();
                }
            } else if (chaosMonkeySettings.getAssaultProperties().isLatencyActive()) {
                generateLatency();
            } else if (chaosMonkeySettings.getAssaultProperties().isExceptionsActive()) {
                generateChaosException();
            } else if (chaosMonkeySettings.getAssaultProperties().isKillApplicationActive()) {
                killTheBossApp();
            }

        }
    }

    private boolean isTrouble() {
        return chaosMonkeySettings.getAssaultProperties().getTroubleRandom() >= chaosMonkeySettings.getAssaultProperties().getLevel();
    }

    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }

    private void killTheBossApp() {

        try {
            LOGGER.info("Chaos Monkey - I am killing your Application!");

            int exit = SpringApplication.exit(context, new ExitCodeGenerator() {
                public int getExitCode() {
                    return 0;
                }
            });
            System.exit(exit);
        } catch (Exception e) {
            LOGGER.info("Chaos Monkey - Unable to kill the App, I am not the BOSS!");
        }
    }

    private void generateChaosException() {
        LOGGER.info("Chaos Monkey - exception");
        throw new RuntimeException("Chaos Monkey - RuntimeException");
    }

    /***
     * Generates a timeout exception
     */
    private void generateLatency() {
        LOGGER.info("Chaos Monkey - timeout");
        int timeout = RandomUtils.nextInt(chaosMonkeySettings.getAssaultProperties().getLatencyRangeStart(), chaosMonkeySettings.getAssaultProperties().getLatencyRangeEnd());

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }


}
