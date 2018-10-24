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

package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Benjamin Wilms
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
@EqualsAndHashCode
@Validated
public class AssaultProperties {

    @Value("${level : 5}")
    @Min(value = 1)
    @Max(value = 10000)
    private int level;

    @Value("${latencyRangeStart : 1000}")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private int latencyRangeStart;

    @Value("${latencyRangeEnd : 3000}")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private int latencyRangeEnd;

    @Value("${latencyActive : true}")
    private boolean latencyActive;

    @Value("${exceptionsActive : false}")
    private boolean exceptionsActive;

    @Value("${killApplicationActive : false}")
    private boolean killApplicationActive;

    @Value("${memoryActive : false}")
    private boolean memoryActive;

    @Value("${memoryKeepFilledLevel : 90000}")
    @Min(value = 15000)
    @Max(value = Integer.MAX_VALUE)
    private int memoryKeepFilledLevel;

    @Value("${memoryIncreaseLevel : 1000}")
    @Min(value = 100)
    @Max(value = 30000)
    private int memoryIncreaseLevel;

    @Value("${memoryMinFreePercentage : 0.15}")
    @DecimalMax("1.0")
    @DecimalMin("0.0")
    private double memoryMinFreePercentage;

    @Value("${memoryFillPercentage : 0.25}")
    @DecimalMax("0.3")
    @DecimalMin("0.05")
    private double memoryFillPercentage;

    @Value("${watchedCustomServices:#{null}}")
    private List<String> watchedCustomServices;

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
        if (watchedCustomServices == null || watchedCustomServices.isEmpty())
            return false;
        return true;
    }


}
