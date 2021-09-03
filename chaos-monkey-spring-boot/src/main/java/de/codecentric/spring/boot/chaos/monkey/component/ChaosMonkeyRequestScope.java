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
 *
 */

package de.codecentric.spring.boot.chaos.monkey.component;

import static org.springframework.util.CollectionUtils.isEmpty;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRequestAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Benjamin Wilms */
public class ChaosMonkeyRequestScope {

  private final ChaosMonkeySettings chaosMonkeySettings;

  private final List<ChaosMonkeyRequestAssault> assaults;
  private final ChaosToggles chaosToggles;
  private final ChaosToggleNameMapper chaosToggleNameMapper;

  private MetricEventPublisher metricEventPublisher;

  private final AtomicInteger assaultCounter;

  public ChaosMonkeyRequestScope(
      ChaosMonkeySettings chaosMonkeySettings,
      List<ChaosMonkeyRequestAssault> assaults,
      List<ChaosMonkeyAssault> legacyAssaults,
      MetricEventPublisher metricEventPublisher,
      ChaosToggles chaosToggles,
      ChaosToggleNameMapper chaosToggleNameMapper) {
    List<RequestAssaultAdapter> assaultAdapters =
        legacyAssaults.stream()
            .filter(
                it ->
                    !(it instanceof ChaosMonkeyRequestAssault
                        || it instanceof ChaosMonkeyRuntimeAssault))
            .map(RequestAssaultAdapter::new)
            .collect(Collectors.toList());
    List<ChaosMonkeyRequestAssault> requestAssaults = new ArrayList<>();
    requestAssaults.addAll(assaults);
    requestAssaults.addAll(assaultAdapters);

    this.chaosMonkeySettings = chaosMonkeySettings;
    this.assaults = requestAssaults;
    this.metricEventPublisher = metricEventPublisher;
    this.chaosToggles = chaosToggles;
    this.chaosToggleNameMapper = chaosToggleNameMapper;
    this.assaultCounter = new AtomicInteger(0);
  }

  public void callChaosMonkey(ChaosTarget type, String simpleName) {
    if (isEnabled(type, simpleName) && isTrouble()) {

      if (metricEventPublisher != null) {
        metricEventPublisher.publishMetricEvent(MetricType.APPLICATION_REQ_COUNT, "type", "total");
      }

      // Custom watched services can be defined at runtime, if there are any, only
      // these will be attacked!
      AssaultProperties assaultProps = chaosMonkeySettings.getAssaultProperties();
      if (assaultProps.isWatchedCustomServicesActive()) {
        if (assaultProps.getWatchedCustomServices().stream().anyMatch(simpleName::startsWith)) {
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
    List<ChaosMonkeyAssault> activeAssaults =
        assaults.stream().filter(ChaosMonkeyAssault::isActive).collect(Collectors.toList());
    if (isEmpty(activeAssaults)) {
      return;
    }
    getRandomFrom(activeAssaults).attack();

    if (metricEventPublisher != null) {
      metricEventPublisher.publishMetricEvent(
          MetricType.APPLICATION_REQ_COUNT, "type", "assaulted");
    }
  }

  private ChaosMonkeyAssault getRandomFrom(List<ChaosMonkeyAssault> activeAssaults) {
    int exceptionRand =
        chaosMonkeySettings.getAssaultProperties().chooseAssault(activeAssaults.size());
    return activeAssaults.get(exceptionRand);
  }

  private boolean isTrouble() {
    if (chaosMonkeySettings.getAssaultProperties().isDeterministic()) {
      return assaultCounter.incrementAndGet()
              % chaosMonkeySettings.getAssaultProperties().getLevel()
          == 0;
    } else {
      return chaosMonkeySettings.getAssaultProperties().getTroubleRandom()
          >= chaosMonkeySettings.getAssaultProperties().getLevel();
    }
  }

  private boolean isEnabled(ChaosTarget type, String name) {
    return this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled()
        && chaosToggles.isEnabled(chaosToggleNameMapper.mapName(type, name));
  }

  private static class RequestAssaultAdapter implements ChaosMonkeyRequestAssault {

    private static final Logger Logger = LoggerFactory.getLogger(RequestAssaultAdapter.class);

    private final ChaosMonkeyAssault rawAssault;

    private RequestAssaultAdapter(ChaosMonkeyAssault rawAssault) {
      Logger.warn(
          "Adapting a "
              + rawAssault.getClass().getSimpleName()
              + " into a request assault. The class should extend its proper parent");

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
}
