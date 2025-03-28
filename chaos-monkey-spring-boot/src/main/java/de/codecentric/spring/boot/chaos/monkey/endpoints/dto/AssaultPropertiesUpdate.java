/*
 * Copyright 2019-2025 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultException;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation.AssaultExceptionConstraint;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation.AssaultPropertiesUpdateLatencyRangeConstraint;
import java.util.List;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * Is used to update properties. Partial updates are allowed: i. e.
 * {@code {"level": 2}} is fine. This is also why we're using ObjectTypes (and
 * not the corresponding primitives).
 */
@Data
@NoArgsConstructor
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@AssaultPropertiesUpdateLatencyRangeConstraint
public class AssaultPropertiesUpdate {
    @Nullable
    @Min(value = 1)
    @Max(value = 10000)
    private Integer level;

    @Nullable
    private Boolean deterministic;

    @Nullable
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer latencyRangeStart;

    @Nullable
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer latencyRangeEnd;

    @Nullable
    private Boolean latencyActive;

    @Nullable
    private Boolean exceptionsActive;

    @AssaultExceptionConstraint
    private AssaultException exception;

    @Nullable
    private Boolean killApplicationActive;

    @Nullable
    private String killApplicationCronExpression;

    @Nullable
    private volatile Boolean memoryActive;

    @Nullable
    @Min(value = 1500)
    @Max(value = Integer.MAX_VALUE)
    private Integer memoryMillisecondsHoldFilledMemory;

    @Nullable
    @Min(value = 100)
    @Max(value = 30000)
    private Integer memoryMillisecondsWaitNextIncrease;

    @Nullable
    @DecimalMax("1.0")
    @DecimalMin("0.0")
    private Double memoryFillIncrementFraction;

    @Nullable
    @DecimalMax("0.95")
    @DecimalMin("0.05")
    private Double memoryFillTargetFraction;

    @Nullable
    private String memoryCronExpression;

    @Nullable
    private Boolean cpuActive;

    @Nullable
    @Min(value = 1500)
    @Max(value = Integer.MAX_VALUE)
    private Integer cpuMillisecondsHoldLoad;

    @Nullable
    @DecimalMax("1.0")
    @DecimalMin("0.1")
    private Double cpuLoadTargetFraction;

    @Nullable
    private String cpuCronExpression;

    /**
     * @deprecated please use {@link #killApplicationCronExpression},
     *             {@link #memoryCronExpression} or {@link #cpuCronExpression}
     *             instead
     */
    @Deprecated
    @Nullable
    private String runtimeAssaultCronExpression;

    @Nullable
    private List<String> watchedCustomServices;

    public void applyTo(AssaultProperties t) {
        try {
            new ObjectMapper().updateValue(t, this);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("cannot update values", e);
        }
    }
}
