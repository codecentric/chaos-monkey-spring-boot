/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import org.junit.jupiter.api.Test;

class AssaultPropertiesUpdateLatencyRangeValidatorTest {

    final AssaultPropertiesUpdateLatencyRangeValidator assaultPropertiesValidator = new AssaultPropertiesUpdateLatencyRangeValidator();

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

    private void validateRange(final Integer rangeStart, final Integer rangeEnd, final boolean expectedValidationResult) {
        final AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setLatencyRangeStart(rangeStart);
        assaultProperties.setLatencyRangeEnd(rangeEnd);

        final boolean valid = assaultPropertiesValidator.isValid(assaultProperties, null);
        assertThat(valid).isEqualTo(expectedValidationResult);
    }
}
