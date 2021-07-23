package de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AssaultExceptionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssaultExceptionConstraint {

  String message() default "Invalid Exception type and arguments";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
