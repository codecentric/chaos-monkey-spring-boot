package de.codecentric.spring.boot.chaos.monkey.endpoints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AssaultPropertiesUpdateLatencyRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssaultPropertiesUpdateLatencyRangeConstraint {

  String message() default
      "Invalid range parameters. Value of latencyRangeStart must not be greater than value of latencyRangeEnd!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
