package de.codecentric.spring.boot.chaos.monkey.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AssaultExceptionValidator implements ConstraintValidator<AssaultExceptionConstraint, AssaultException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssaultExceptionValidator.class);

    @Override
    public boolean isValid(AssaultException exception, ConstraintValidatorContext constraintValidatorContext) {
        if (exception == null) {
            return true;
        }

        try {
            Class<? extends RuntimeException> exceptionClass = exception.getExceptionClass();
            if (exception.getArguments() == null) {
                exceptionClass.getConstructor();
            } else {
                exceptionClass.getConstructor(exception.getExceptionArgumentTypes().toArray(new Class[0]));
            }
            return true;
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Invalid combination of type ({}) and arguments provided", exception.getType());
        }
        return false;
    }
}
