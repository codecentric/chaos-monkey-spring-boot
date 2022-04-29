/*
 * Copyright 2018-2022 the original author or authors.
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

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssaultExceptionValidator implements ConstraintValidator<AssaultExceptionConstraint, AssaultException> {

    private static final Logger Logger = LoggerFactory.getLogger(AssaultExceptionValidator.class);

    @Override
    public boolean isValid(AssaultException exception, ConstraintValidatorContext constraintValidatorContext) {
        if (exception == null) {
            return true;
        }

        try {
            exception.getCreator();
            return true;
        } catch (ReflectiveOperationException e) {
            Logger.warn("Invalid combination of type ({}), method and arguments provided", exception.getType());
        }
        return false;
    }
}
