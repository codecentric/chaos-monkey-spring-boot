package de.codecentric.spring.boot.chaos.monkey.configuration;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AssaultPropertiesLatencyRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssaultPropertiesLatencyRangeConstraint {
    String message() default "Invalid range parameters. Value of latencyRangeStart must not be greater than value of latencyRangeEnd!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
