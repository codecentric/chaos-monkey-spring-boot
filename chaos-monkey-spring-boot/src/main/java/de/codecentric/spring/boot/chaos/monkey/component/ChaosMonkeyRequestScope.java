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
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRequestAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Benjamin Wilms
 */
public class ChaosMonkeyRequestScope {

    private final ChaosMonkeySettings chaosMonkeySettings;
    private final List<ChaosMonkeyRequestAssault> assaults;
    private MetricEventPublisher metricEventPublisher;

    private static class RequestAssaultAdapter implements ChaosMonkeyRequestAssault {

        private static final Logger LOGGER = LoggerFactory.getLogger(RequestAssaultAdapter.class);
        private final ChaosMonkeyAssault rawAssault;


        private RequestAssaultAdapter(ChaosMonkeyAssault rawAssault) {
            LOGGER.warn("Adapting a " + rawAssault.getClass().getSimpleName() + " into a request assault. The class should extend its proper parent");

            this.rawAssault = rawAssault;
        }

        @Override
        public boolean isActive() {
            return rawAssault.isActive();
        }

        @Override
        public void attack() {
            rawAssault.attack();
        }
    }

    public ChaosMonkeyRequestScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyRequestAssault> assaults,
                                   List<ChaosMonkeyAssault> legacyAssaults,
                                   MetricEventPublisher metricEventPublisher) {
        List<RequestAssaultAdapter> assaultAdapters = legacyAssaults.stream().
                filter(it -> !(it instanceof ChaosMonkeyRequestAssault || it instanceof ChaosMonkeyRuntimeAssault)).
                map(RequestAssaultAdapter::new).
                collect(Collectors.toList());
        List<ChaosMonkeyRequestAssault> requestAssaults = new ArrayList<>();
        requestAssaults.addAll(assaults);
        requestAssaults.addAll(assaultAdapters);


        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = requestAssaults;
        this.metricEventPublisher = metricEventPublisher;
    }


    public void callChaosMonkey(String simpleName) {
        if (isEnabled() && isTrouble()) {

            if (metricEventPublisher != null)
                metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");

            // Custom watched services can be defined at runtime, if there are any, only these will be attacked!
            if (chaosMonkeySettings.getAssaultProperties().isWatchedCustomServicesActive()) {
                if (chaosMonkeySettings.getAssaultProperties().getWatchedCustomServices().contains(simpleName)) {
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