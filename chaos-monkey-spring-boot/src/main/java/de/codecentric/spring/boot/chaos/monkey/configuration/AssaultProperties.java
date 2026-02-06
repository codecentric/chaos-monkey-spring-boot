/*
 * Copyright 2018-2026 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.databind.ObjectMapper;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
public class AssaultProperties {
    private int level = 1;

    private boolean deterministic = false;

    private int latencyRangeStart = 1000;

    private int latencyRangeEnd = 3000;

    private boolean latencyActive = false;

    private boolean exceptionsActive = false;

    @NestedConfigurationProperty
    private AssaultException exception;

    private boolean killApplicationActive = false;

    private String killApplicationCronExpression = "OFF";

    private volatile boolean memoryActive = false;

    private int memoryMillisecondsHoldFilledMemory = 90000;

    private int memoryMillisecondsWaitNextIncrease = 1000;

    private double memoryFillIncrementFraction = 0.15;

    private double memoryFillTargetFraction = 0.25;

    private String memoryCronExpression = "OFF";

    private volatile boolean cpuActive = false;

    private int cpuMillisecondsHoldLoad = 90000;

    private double cpuLoadTargetFraction = 0.9;

    private String cpuCronExpression = "OFF";

    private List<String> watchedCustomServices;

    public AssaultException getException() {
        return exception == null ? new AssaultException() : exception;
    }

    public void setException(AssaultException exception) {
        this.exception = exception;
    }

    @JsonIgnore
    public int getTroubleRandom() {
        return ThreadLocalRandom.current().nextInt(1, getLevel() + 1);
    }

    @JsonIgnore
    public int chooseAssault(int amount) {
        return ThreadLocalRandom.current().nextInt(0, amount);
    }

    @JsonIgnore
    public boolean isWatchedCustomServicesActive() {
        return !CollectionUtils.isEmpty(watchedCustomServices);
    }

    public AssaultPropertiesUpdate toDto() {
        return new ObjectMapper().convertValue(this, AssaultPropertiesUpdate.class);
    }
}
