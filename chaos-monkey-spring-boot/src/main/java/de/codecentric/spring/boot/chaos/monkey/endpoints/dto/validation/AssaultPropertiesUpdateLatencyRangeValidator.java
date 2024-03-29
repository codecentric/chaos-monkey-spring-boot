/*
 * Copyright 2021-2023 the original author or authors.
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

import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AssaultPropertiesUpdateLatencyRangeValidator
        implements
            ConstraintValidator<AssaultPropertiesUpdateLatencyRangeConstraint, AssaultPropertiesUpdate> {

    @Override
    public boolean isValid(final AssaultPropertiesUpdate properties, final ConstraintValidatorContext constraintValidatorContext) {
        Integer start = properties.getLatencyRangeStart();
        Integer end = properties.getLatencyRangeEnd();
        boolean isEmptyRange = start == null && end == null;
        boolean isCompleteRange = start != null && end != null;
        return isEmptyRange || (isCompleteRange && start <= end);
    }
}
