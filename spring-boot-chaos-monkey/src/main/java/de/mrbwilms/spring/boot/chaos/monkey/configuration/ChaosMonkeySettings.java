package de.mrbwilms.spring.boot.chaos.monkey.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Benjamin Wilms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChaosMonkeySettings {

   private AssaultProperties assaultProperties;
   private WatcherProperties watcherProperties;



}
