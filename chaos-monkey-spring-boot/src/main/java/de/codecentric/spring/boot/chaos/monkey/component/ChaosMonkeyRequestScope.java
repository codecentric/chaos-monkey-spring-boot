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

import de.codecentric.spring.boot.chaos.monkey.assaults.AssaultType;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRequestScope {

    private final ChaosMonkeySettings chaosMonkeySettings;
    private final List<ChaosMonkeyAssault> assaults;
    private MetricEventPublisher metricEventPublisher;

    public ChaosMonkeyRequestScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyAssault> assaults, MetricEventPublisher metricEventPublisher) {
        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = assaults;
        this.metricEventPublisher = metricEventPublisher;
    }


    public void callChaosMonkey(String simpleName) {
        if (isEnabled() && isTrouble()) {

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");

            // Custom watched services can be defined at runtime, if there are any, only these will be attacked!
            if (chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive()) {
                if (!chaosMonkeySettings.getAssaultProperties().getWatchedCustomServices().isEmpty() && chaosMonkeySettings.getAssaultProperties().getWatchedCustomServices().contains(simpleName)) {
                    // only all listed custom methods will be attacked
                    chooseAndRunAttack();
                }
            } else {
                // default attack if no custom watched service is defined
                chooseAndRunAttack();
            }
        }

    }

    private void chooseAndRunAttack() {
        List<ChaosMonkeyAssault> activeAssaults = assaults.stream()
                .filter(chaosMonkeyAssault -> chaosMonkeyAssault.getAssaultType() == AssaultType.REQUEST)
                .filter(ChaosMonkeyAssault::isActive)
                .collect(Collectors.toList());
        if (isEmpty(activeAssaults)) {
            return;
        }
        getRandomFrom(activeAssaults).attack();

        if (metricEventPublisher != null)
            metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
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