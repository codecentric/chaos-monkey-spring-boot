package de.mrbw.chaos.monkey;

import java.lang.annotation.*;

import de.mrbw.chaos.monkey.configuration.ChaosMonkeyConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author Benjamin Wilms
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(ChaosMonkeyConfiguration.class)
public @interface EnableChaosMonkey {
}
