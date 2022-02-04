package de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import org.junit.jupiter.api.Test;

class AssaultPropertiesUpdateLatencyRangeValidatorTest {

  final AssaultPropertiesUpdateLatencyRangeValidator assaultPropertiesValidator =
      new AssaultPropertiesUpdateLatencyRangeValidator();

  @Test
  void rangeStartSmallerThanRangeEndIsValid() {
    validateRange(1000, 1001, true);
  }

  @Test
  void rangeStartAsBigAsRangeEndIsValid() {
    validateRange(1000, 1000, true);
  }

  @Test
  void rangeStartBiggerThanRangeEndIsNotValid() {
    validateRange(1001, 1000, false);
  }

  @Test
  void noRangeIsValid() {
    validateRange(null, null, true);
  }

  @Test
  void onlyRangeStartIsNotValid() {
    validateRange(1000, null, false);
  }

  @Test
  void onlyRangeEndIsNotValid() {
    validateRange(null, 1000, false);
  }

  private void validateRange(
      final Integer rangeStart, final Integer rangeEnd, final boolean expectedValidationResult) {
    final AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
    assaultProperties.setLatencyRangeStart(rangeStart);
    assaultProperties.setLatencyRangeEnd(rangeEnd);

    final boolean valid = assaultPropertiesValidator.isValid(assaultProperties, null);
    assertThat(valid).isEqualTo(expectedValidationResult);
  }
}
