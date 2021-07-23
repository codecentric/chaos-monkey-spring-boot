package de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation;

import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AssaultPropertiesUpdateLatencyRangeValidator
    implements ConstraintValidator<
        AssaultPropertiesUpdateLatencyRangeConstraint, AssaultPropertiesUpdate> {

  @Override
  public boolean isValid(
      final AssaultPropertiesUpdate properties,
      final ConstraintValidatorContext constraintValidatorContext) {
    Integer start = properties.getLatencyRangeStart();
    Integer end = properties.getLatencyRangeEnd();
    boolean isEmptyRange = start == null && end == null;
    boolean isCompleteRange = start != null && end != null;
    return isEmptyRange || (isCompleteRange && start <= end);
  }
}
