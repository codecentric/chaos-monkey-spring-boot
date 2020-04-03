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

import static de.codecentric.spring.boot.chaos.monkey.configuration.AssaultPropertyMinMaxValidator.of;
import static java.lang.Integer.MAX_VALUE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** @author Benjamin Wilms */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
@EqualsAndHashCode
@AssaultPropertiesLatencyRangeConstraint
public class AssaultProperties implements Validator {
  @Value("${level : 1}")
  private int level;

  @Value("${latencyRangeStart : 1000}")
  private int latencyRangeStart;

  @Value("${latencyRangeEnd : 3000}")
  private int latencyRangeEnd;

  @Value("${latencyActive : false}")
  private boolean latencyActive;

  @Value("${exceptionsActive : false}")
  private boolean exceptionsActive;

  @AssaultExceptionConstraint private AssaultException exception;

  @Value("${killApplicationActive : false}")
  private boolean killApplicationActive;

  @Value("${memoryActive : false}")
  private volatile boolean memoryActive;

  @Value("${memoryMillisecondsHoldFilledMemory : 90000}")
  private int memoryMillisecondsHoldFilledMemory;

  @Value("${memoryMillisecondsWaitNextIncrease : 1000}")
  private int memoryMillisecondsWaitNextIncrease;

  @Value("${memoryFillIncrementFraction : 0.15}")
  private double memoryFillIncrementFraction;

  @Value("${memoryFillTargetFraction : 0.25}")
  private double memoryFillTargetFraction;

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

  @Override
  public boolean supports(Class<?> clazz) {
    return AssaultProperties.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (target == null) {
      return;
    }
    AssaultProperties properties = (AssaultProperties) target;

    getPropertiesToValidate(properties)
        .filter(AssaultPropertyMinMaxValidator::isInvalid)
        .forEach(
            invalid -> {
              errors.rejectValue(
                  invalid.getPropertyName(),
                  invalid.getPropertyName() + ".invalid",
                  invalid.getValidationErrorMessage());
            });
  }

  private Stream<AssaultPropertyMinMaxValidator> getPropertiesToValidate(
      AssaultProperties properties) {
    List<AssaultPropertyMinMaxValidator> toValidate = new ArrayList<>();
    toValidate.add(of(properties.getLevel(), 1, 10000, "level"));
    toValidate.add(of(properties.getLatencyRangeStart(), 1, MAX_VALUE, "latencyRangeStart"));
    toValidate.add(of(properties.getLatencyRangeEnd(), 1, MAX_VALUE, "latencyRangeEnd"));
    toValidate.add(
        of(
            properties.getMemoryMillisecondsHoldFilledMemory(),
            1500,
            MAX_VALUE,
            "memoryMillisecondsHoldFilledMemory"));
    toValidate.add(
        of(
            properties.getMemoryMillisecondsWaitNextIncrease(),
            100,
            30000,
            "memoryMillisecondsWaitNextIncrease"));
    toValidate.add(
        of(properties.getMemoryFillIncrementFraction(), 0.01, 1.0, "memoryFillIncrementFraction"));
    toValidate.add(
        of(properties.getMemoryFillTargetFraction(), 0.01, 1.0, "memoryFillTargetFraction"));
    return toValidate.stream();
  }
}
