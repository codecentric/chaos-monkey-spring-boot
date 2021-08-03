package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ChaosMonkeyDisabledDto {
  private final String status = "Chaos Monkey is disabled";
  private ZonedDateTime disabledAt = ZonedDateTime.now();
}
