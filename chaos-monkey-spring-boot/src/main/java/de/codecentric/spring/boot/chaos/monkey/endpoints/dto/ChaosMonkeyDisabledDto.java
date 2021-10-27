package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ChaosMonkeyDisabledDto {
  private final String status = "Chaos Monkey is disabled";
  private final ZonedDateTime disabledAt = ZonedDateTime.now();
  @JsonIgnore private Duration timeActive;
  @JsonIgnore private Duration timeInActive;
  private String timeActiveFormatted;
  private String timeInActiveFormatted;

  public ChaosMonkeyDisabledDto() {
    this.timeActive = Duration.ZERO;
  }

  public ChaosMonkeyDisabledDto(ChaosMonkeyEnabledDto chaosMonkeyEnabledDto) {
    this.timeActive = chaosMonkeyEnabledDto.getTimeActive();
  }

  public Duration getTimeInActive() {
    timeInActive = Duration.between(disabledAt, ZonedDateTime.now());
    return timeInActive;
  }

  public String getTimeActiveFormatted() {
    return formatDuration(timeActive);
  }

  public String getTimeInActiveFormatted() {
    return formatDuration(getTimeInActive());
  }

  private String formatDuration(Duration duration) {
    long inSeconds = duration.getSeconds();
    long secondsPart = inSeconds % 60;
    long minutesPart = (inSeconds % 3600) / 60;

    if (duration.toHours() > 0) {
      return String.format(
          "%d hours %02d minutes %02d seconds", duration.toHours(), minutesPart, secondsPart);
    } else if (duration.toMinutes() > 0) {
      return String.format("%d minutes %02d seconds", duration.toMinutes(), secondsPart);
    } else {
      return String.format("%d seconds", inSeconds);
    }
  }
}
