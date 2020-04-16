package de.codecentric.spring.boot.chaos.monkey.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultException;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.List;
import java.util.function.Consumer;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;


/**
 * Is used to update properties. Partial updates are allowed:
 * i. e. {@code {"level": 2}} is fine.
 * This is also why we're using ObjectTypes (and not the corresponding primitives).
 **/
@Data
@NoArgsConstructor
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssaultPropertiesUpdate {
  @Nullable
  @Min(value = 1)
  @Max(value = 10000)
  private Integer level;

  private Integer latencyRangeStart;

  private Integer latencyRangeEnd;

  private Boolean latencyActive;

  private Boolean exceptionsActive;

  private AssaultException exception;

  private Boolean killApplicationActive;

  private volatile Boolean memoryActive;

  private Integer memoryMillisecondsHoldFilledMemory;

  private Integer memoryMillisecondsWaitNextIncrease;

  private Double memoryFillIncrementFraction;

  private Double memoryFillTargetFraction;

  private String runtimeAssaultCronExpression;

  private List<String> watchedCustomServices;

  private <T> void applyTo(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  public void applyTo(AssaultProperties t) {
    this.applyTo(level, t::setLevel);
    this.applyTo(latencyActive, t::setLatencyActive);
    this.applyTo(latencyRangeStart, t::setLatencyRangeStart);
    applyTo(latencyRangeEnd, t::setLatencyRangeEnd);

    applyTo(exceptionsActive, t::setExceptionsActive);
    applyTo(exception, t::setException);

    applyTo(killApplicationActive, t::setKillApplicationActive);

    applyTo(memoryActive, t::setMemoryActive);
    applyTo(memoryMillisecondsHoldFilledMemory, t::setMemoryMillisecondsHoldFilledMemory);
    applyTo(memoryMillisecondsWaitNextIncrease, t::setMemoryMillisecondsWaitNextIncrease);
    applyTo(memoryFillIncrementFraction, t::setMemoryFillIncrementFraction);
    applyTo(memoryFillTargetFraction, t::setMemoryFillTargetFraction);
    applyTo(runtimeAssaultCronExpression, t::setRuntimeAssaultCronExpression);
    applyTo(watchedCustomServices, t::setWatchedCustomServices);
  }
}
