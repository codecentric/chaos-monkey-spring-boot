package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ChaosMonkeyStatusResponseDto {

  private boolean isEnabled;
  private OffsetDateTime enabledAt;
  private OffsetDateTime disabledAt;
  private Value<Duration> enabledFor;

  public ChaosMonkeyStatusResponseDto(
      boolean isEnabled, @Nullable Long lastStatusToggleTimestamp, @Nullable Duration enabledFor) {
    this.isEnabled = isEnabled;
    OffsetDateTime lastStatusToggleTime =
        lastStatusToggleTimestamp != null
            ? OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(lastStatusToggleTimestamp), ZoneId.systemDefault())
            : null;
    this.enabledAt = isEnabled ? lastStatusToggleTime : null;
    this.disabledAt = !isEnabled ? lastStatusToggleTime : null;
    this.enabledFor =
        enabledFor != null ? new Value<>(enabledFor, formatDuration(enabledFor)) : null;
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

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Value<T> {

    private T raw;
    private String formatted;
  }
}
