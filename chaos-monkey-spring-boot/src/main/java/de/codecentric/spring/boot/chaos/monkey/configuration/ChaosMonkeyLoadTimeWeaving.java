package de.codecentric.spring.boot.chaos.monkey.configuration;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.LoadTimeWeavingConfiguration;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;

/** @author Benjamin Wilms */
@Configuration
@Conditional(ChaosMonkeyCondition.class)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class ChaosMonkeyLoadTimeWeaving extends LoadTimeWeavingConfiguration {

  @Override
  public LoadTimeWeaver loadTimeWeaver() {
    return new ReflectiveLoadTimeWeaver();
  }
}
