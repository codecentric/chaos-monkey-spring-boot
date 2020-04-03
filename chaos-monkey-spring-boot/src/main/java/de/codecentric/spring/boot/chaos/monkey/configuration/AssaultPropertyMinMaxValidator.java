package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.Data;

@Data
public class AssaultPropertyMinMaxValidator {

  private static final String VALIDATION_ERROR_MESSAGE = "%s needs to be between %s and %s";
  private final String validationErrorMessage;
  private final String propertyName;
  private final boolean isInvalid;

  public AssaultPropertyMinMaxValidator(
      boolean isInvalid, String propertyName, String validationErrorMessage) {
    this.propertyName = propertyName;
    this.validationErrorMessage = validationErrorMessage;
    this.isInvalid = isInvalid;
  }

  public static AssaultPropertyMinMaxValidator of(
      int propertyValue, int min, int max, String propertyName) {
    if (propertyValue < min || propertyValue > max) {
      String validationErrorMessage =
          String.format(VALIDATION_ERROR_MESSAGE, propertyName, min, max);
      return new AssaultPropertyMinMaxValidator(true, propertyName, validationErrorMessage);
    }
    return new AssaultPropertyMinMaxValidator(false, propertyName, "");
  }

  public static AssaultPropertyMinMaxValidator of(
      double propertyValue, double min, double max, String propertyName) {
    if (propertyValue < min || propertyValue > max) {
      String validationErrorMessage =
          String.format(VALIDATION_ERROR_MESSAGE, propertyName, min, max);
      return new AssaultPropertyMinMaxValidator(true, propertyName, validationErrorMessage);
    }
    return new AssaultPropertyMinMaxValidator(false, propertyName, "");
  }
}
