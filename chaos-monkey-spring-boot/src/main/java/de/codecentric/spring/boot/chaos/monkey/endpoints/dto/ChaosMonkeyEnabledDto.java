package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ChaosMonkeyEnabledDto {
  private final String status = "Chaos Monkey is enabled";
  private final ZonedDateTime enabledAt = ZonedDateTime.now();
  @JsonIgnore private Duration timeActive;
  private String timeActiveFormatted;

  public Duration getTimeActive() {
    timeActive = Duration.between(enabledAt, ZonedDateTime.now());
    return timeActive;
  }

  public String getTimeActiveFormatted() {
    return formatDuration(getTimeActive());
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
