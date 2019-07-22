package de.codecentric.spring.boot.chaos.monkey.configuration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AssaultPropertiesLatencyRangeValidator implements ConstraintValidator<AssaultPropertiesLatencyRangeConstraint, AssaultProperties> {

    @Override
    public boolean isValid(final AssaultProperties assaultProperties,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (assaultProperties == null) {
            return true;
        }

        return assaultProperties.getLatencyRangeStart() <= assaultProperties.getLatencyRangeEnd();
    }

}
