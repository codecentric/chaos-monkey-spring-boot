package de.mrbwilms.spring.boot.chaos.monkey.conditions;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition to attack all <b>public</b> methods in classes annotated with @{@link org.springframework.stereotype.Controller}
 *
 * @author Benjamin Wilms
 */
public class AttackControllerCondition implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return
                context.getEnvironment()
                .getProperty("chaos.monkey.attack.controller","false")
                .matches("(?i:.*true*)");
    }
}
