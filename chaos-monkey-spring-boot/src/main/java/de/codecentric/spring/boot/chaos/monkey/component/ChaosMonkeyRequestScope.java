/*
 * Copyright 2018-2025 the original author or authors.
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

import static org.springframework.util.CollectionUtils.isEmpty;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRequestAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.MethodFilter;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin Wilms
 */
@Slf4j
public class ChaosMonkeyRequestScope {

    private final ChaosMonkeySettings chaosMonkeySettings;

    private final List<ChaosMonkeyRequestAssault> assaults;
    private final ChaosToggles chaosToggles;
    private final ChaosToggleNameMapper chaosToggleNameMapper;

    private final MetricEventPublisher metricEventPublisher;

    private final AtomicInteger assaultCounter;
    private final MethodFilter methodFilter;

    public ChaosMonkeyRequestScope(ChaosMonkeySettings chaosMonkeySettings, List<ChaosMonkeyRequestAssault> assaults,
            List<ChaosMonkeyAssault> legacyAssaults, MetricEventPublisher metricEventPublisher, ChaosToggles chaosToggles,
            ChaosToggleNameMapper chaosToggleNameMapper, MethodFilter methodFilter) {
        this.methodFilter = methodFilter;
        List<ChaosMonkeyRequestAssault> requestAssaults = new ArrayList<>(assaults);
        requestAssaults.addAll(getLegacyAssaults(legacyAssaults));

        this.chaosMonkeySettings = chaosMonkeySettings;
        this.assaults = requestAssaults;
        this.metricEventPublisher = metricEventPublisher;
        this.chaosToggles = chaosToggles;
        this.chaosToggleNameMapper = chaosToggleNameMapper;
        this.assaultCounter = new AtomicInteger(0);
    }

    private static List<RequestAssaultAdapter> getLegacyAssaults(List<ChaosMonkeyAssault> legacyAssaults) {
        return legacyAssaults.stream().filter(Predicate.not(ChaosMonkeyRuntimeAssault.class::isInstance))
                .filter(Predicate.not(ChaosMonkeyRequestAssault.class::isInstance)).map(RequestAssaultAdapter::new).toList();
    }

    public void callChaosMonkey(ChaosTarget type, String signature) {
        callChaosMonkey(type, signature, null, null);
    }

    public void callChaosMonkey(ChaosTarget type, String signature, Object target, Method method) {
        if (isEnabled(type, signature) && isTrouble()) {
            metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");

            // Custom watched services can be defined at runtime, if there are any, only
            // these will be attacked!
            AssaultProperties assaultProps = chaosMonkeySettings.getAssaultProperties();
            if (!assaultProps.isWatchedCustomServicesActive() || assaultProps.getWatchedCustomServices().stream().anyMatch(signature::startsWith)) {
                // only all listed custom methods will be attacked
                // default attack if no custom watched service is defined
                chooseAndRunAttack(target, method);
            }
        }
    }

    private void chooseAndRunAttack(Object target, Method method) {
        List<ChaosMonkeyAssault> activeAssaults = assaults.stream().filter(ChaosMonkeyAssault::isActive).collect(Collectors.toList());
        if (isEmpty(activeAssaults)) {
            return;
        }

        ChaosMonkeyAssault assault = getRandomFrom(activeAssaults);
        if (target != null && method != null && assault instanceof ExceptionAssault) {
            log.info("exception assault found");
            if (methodFilter.filter(target, method)) {
                log.info("recover found");
                return;
            }
        }
        assault.attack();

        metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }

    private ChaosMonkeyAssault getRandomFrom(List<? extends ChaosMonkeyAssault> activeAssaults) {
        return activeAssaults.get(chaosMonkeySettings.getAssaultProperties().chooseAssault(activeAssaults.size()));
    }

    private boolean isTrouble() {
        AssaultProperties assaultProperties = chaosMonkeySettings.getAssaultProperties();
        if (assaultProperties.isDeterministic()) {
            return assaultCounter.incrementAndGet() % assaultProperties.getLevel() == 0;
        }
        return assaultProperties.getTroubleRandom() >= assaultProperties.getLevel();
    }

    private boolean isEnabled(ChaosTarget type, String name) {
        return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled() && chaosToggles.isEnabled(chaosToggleNameMapper.mapName(type, name));
    }

    private record RequestAssaultAdapter(ChaosMonkeyAssault rawAssault) implements ChaosMonkeyRequestAssault {

        private static final Logger Logger = LoggerFactory.getLogger(RequestAssaultAdapter.class);

        private RequestAssaultAdapter {
            Logger.warn("Adapting a {} into a request assault. The class should extend its proper parent", rawAssault.getClass().getSimpleName());
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
}
