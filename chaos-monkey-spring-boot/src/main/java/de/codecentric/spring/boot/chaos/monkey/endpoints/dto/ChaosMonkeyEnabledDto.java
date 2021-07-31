package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ChaosMonkeyEnabledDto {
  private final String status = "Chaos Monkey is enabled";
  private ZonedDateTime enabledAt = ZonedDateTime.now();
}
