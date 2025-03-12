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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AssaultPropertiesLatencyRangeValidatorTest {

    final AssaultPropertiesUpdateLatencyRangeValidator assaultPropertiesValidator = new AssaultPropertiesUpdateLatencyRangeValidator();

    @ParameterizedTest
    @CsvSource(value = {
            "1000, 1001",
            "1000, 1000",
            "NULL, NULL"
    }, nullValues = "NULL")
    void valideRange(Integer rangeStart, Integer rangeEnd) {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setLatencyRangeStart(rangeStart);
        assaultProperties.setLatencyRangeEnd(rangeEnd);

        assertTrue(assaultPropertiesValidator.isValid(assaultProperties, null));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1001, 1000",
            "1000, NULL",
            "NULL, 1000"
    }, nullValues = "NULL")
    void invalideRange(final Integer rangeStart, final Integer rangeEnd) {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setLatencyRangeStart(rangeStart);
        assaultProperties.setLatencyRangeEnd(rangeEnd);

        assertFalse(assaultPropertiesValidator.isValid(assaultProperties, null));
    }
}
