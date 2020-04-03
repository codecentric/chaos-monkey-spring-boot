package de.codecentric.spring.boot.chaos.monkey.configuration;

import static de.codecentric.spring.boot.chaos.monkey.configuration.AssaultPropertyMinMaxValidator.of;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AssaultPropertyMinMaxValidatorTest {

  @Nested
  class IsInRange {
    @Test
    void inBetweenExpectIsValid() {
      assertThat(of(10, 1, 20, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isMaxExpectIsValid() {
      assertThat(of(20, 1, 20, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isMinExpectIsValid() {
      assertThat(of(1, 1, 20, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isErrorMessageEmpty() {
      assertThat(of(10, 1, 20, "validProp").getValidationErrorMessage().isEmpty()).isTrue();
    }

    @Test
    void isPropertyNameEquals() {
      assertThat(of(10, 1, 20, "validProp").getPropertyName()).isEqualTo("validProp");
    }
  }

  @Nested
  class IsInRangeDecimal {
    @Test
    void inBetweenExpectIsValid() {
      assertThat(of(1.5, 0.9, 1.8, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isMaxExpectIsValid() {
      assertThat(of(1.8, 0.9, 1.8, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isMinExpectIsValid() {
      assertThat(of(0.9, 0.9, 1.8, "validProp").isInvalid()).isFalse();
    }

    @Test
    void isErrorMessageEmpty() {
      assertThat(of(0.9, 0.9, 1.8, "validProp").getValidationErrorMessage().isEmpty()).isTrue();
    }

    @Test
    void isPropertyNameEquals() {
      assertThat(of(0.9, 0.9, 1.8, "validProp").getPropertyName()).isEqualTo("validProp");
    }
  }

  @Nested
  class IsOutOfRange {

    private AssaultPropertyMinMaxValidator invalid;

    @BeforeEach
    void setUp() {
      invalid = of(11, 1, 10, "invalidProp");
    }

    @Test
    void expectIsInvalid() {
      assertThat(invalid.isInvalid()).isTrue();
    }

    @Test
    void isPropertyNameEquals() {
      assertThat(invalid.getPropertyName()).isEqualTo("invalidProp");
    }

    @Test
    void isErrorMessageFilled() {
      assertThat(invalid.getValidationErrorMessage())
          .isEqualTo("invalidProp needs to be between 1 and 10");
    }
  }

  @Nested
  class IsOutOfRangeDecimal {

    private AssaultPropertyMinMaxValidator invalid;

    @BeforeEach
    void setUp() {
      invalid = of(1.9, 0.9, 1.8, "invalidProp");
    }

    @Test
    void expectIsInvalid() {
      assertThat(invalid.isInvalid()).isTrue();
    }

    @Test
    void isPropertyNameEquals() {
      assertThat(invalid.getPropertyName()).isEqualTo("invalidProp");
    }

    @Test
    void isErrorMessageFilled() {
      assertThat(invalid.getValidationErrorMessage())
          .isEqualTo("invalidProp needs to be between 0.9 and 1.8");
    }
  }
}
