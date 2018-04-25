package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Benjamin Wilms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChaosMonkeySettings {

   private ChaosMonkeyProperties chaosMonkeyProperties;
   private AssaultProperties assaultProperties;
   private WatcherProperties watcherProperties;

}
