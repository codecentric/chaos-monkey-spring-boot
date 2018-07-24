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

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkey {

    private ChaosMonkeySettings chaosMonkeySettings;

    private List<ChaosMonkeyAssault> assaults;
    private final Metrics metrics;

    public ChaosMonkey(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyAssault> assaults, Metrics metrics) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = assaults;
        this.metrics = metrics;
    }

    public void callChaosMonkey() {
        if (isEnabled() && metrics != null)
            metrics.counter(MetricType.APPLICATION_REQ_COUNT,"count","total").increment();

        if (isTrouble() && isEnabled()) {

            List<ChaosMonkeyAssault> activeAssaults = assaults.stream()
                    .filter(ChaosMonkeyAssault::isActive)
                    .collect(Collectors.toList());
            if (isEmpty(activeAssaults)) {
                return;
            }
            getRandomFrom(activeAssaults).attack();
            // attacked requests
            if (metrics != null) {
                metrics.counter(MetricType.APPLICATION_REQ_COUNT, "count", "assaulted").increment();
            }
        }

    }

    private ChaosMonkeyAssault getRandomFrom(List<ChaosMonkeyAssault> activeAssaults) {
        int exceptionRand = chaosMonkeySettings.getAssaultProperties().chooseAssault(activeAssaults.size());
        return activeAssaults.get(exceptionRand);
    }

    private boolean isTrouble() {
        return chaosMonkeySettings.getAssaultProperties().getTroubleRandom() >= chaosMonkeySettings.getAssaultProperties().getLevel();
    }

    private boolean isEnabled() {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled();
    }
}