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
import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.spring.boot.chaos.monkey.endpoints.AssaultPropertiesUpdate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Benjamin Wilms
 * @author Maxime Bouchenoire
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
@Validated
@EqualsAndHashCode
@AssaultPropertiesLatencyRangeConstraint
public class AssaultProperties {
  @Value("${level : 1}")
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

  @Value("${latencyActive : false}")
  private boolean latencyActive;

  @Value("${exceptionsActive : false}")
  private boolean exceptionsActive;

  @AssaultExceptionConstraint private AssaultException exception;

  @Value("${killApplicationActive : false}")
  private boolean killApplicationActive;

  @Value("${killApplication.cron.expression:OFF}")
  private String killApplicationCronExpression;

  @Value("${memoryActive : false}")
  private volatile boolean memoryActive;

  @Value("${memoryMillisecondsHoldFilledMemory : 90000}")
  @Min(value = 1500)
  @Max(value = Integer.MAX_VALUE)
  private int memoryMillisecondsHoldFilledMemory;

  @Value("${memoryMillisecondsWaitNextIncrease : 1000}")
  @Min(value = 100)
  @Max(value = 30000)
  private int memoryMillisecondsWaitNextIncrease;

  @Value("${memoryFillIncrementFraction : 0.15}")
  @DecimalMax("1.0")
  @DecimalMin("0.01")
  private double memoryFillIncrementFraction;

  @Value("${memoryFillTargetFraction : 0.25}")
  @DecimalMax("1.0")
  @DecimalMin("0.01")
  private double memoryFillTargetFraction;

  @Value("${memory.cron.expression:OFF}")
  private String memoryCronExpression;

  @Value("${runtime.scope.assault.cron.expression:OFF}")
  private String runtimeAssaultCronExpression;

  @Value("${watchedCustomServices:#{null}}")
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
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(this, AssaultPropertiesUpdate.class);
  }
}
