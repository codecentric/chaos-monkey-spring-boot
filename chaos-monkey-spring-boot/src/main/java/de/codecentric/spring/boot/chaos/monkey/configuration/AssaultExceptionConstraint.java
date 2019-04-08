package de.codecentric.spring.boot.chaos.monkey.configuration;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AssaultExceptionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssaultExceptionConstraint {
    String message() default "Invalid Exception type and arguments";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

