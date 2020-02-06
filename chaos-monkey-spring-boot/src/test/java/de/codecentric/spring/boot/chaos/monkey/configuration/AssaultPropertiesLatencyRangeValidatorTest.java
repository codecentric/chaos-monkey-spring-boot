package de.codecentric.spring.boot.chaos.monkey.configuration;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

class AssaultPropertiesLatencyRangeValidatorTest {

  final AssaultPropertiesLatencyRangeValidator assaultPropertiesValidator =
      new AssaultPropertiesLatencyRangeValidator();

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

  private void validateRange(
      final int rangeStart, final int rangeEnd, final boolean expectedValidationResult) {
    final AssaultProperties assaultProperties = new AssaultProperties();
    assaultProperties.setLatencyRangeStart(rangeStart);
    assaultProperties.setLatencyRangeEnd(rangeEnd);

    final boolean valid = assaultPropertiesValidator.isValid(assaultProperties, null);
    assertThat(valid, CoreMatchers.is(expectedValidationResult));
  }
}
