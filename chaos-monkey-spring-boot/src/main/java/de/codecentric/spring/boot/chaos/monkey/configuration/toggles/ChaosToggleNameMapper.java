package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;

/**
 * A way to map individual ChaosTargets (controllers, repositories, etc) and the corresponding
 * method. Implementations can make the name to toggle mapping as coarse or as detailed as desired.
 *
 * @author Clint Checketts
 */
public interface ChaosToggleNameMapper {

  /**
   * @param type ChaosType (controller, repository, etc)
   * @param name Name of item being assaulted (a method class and method name for example)
   * @return the toggle name to be switched
   */
  String mapName(ChaosTarget type, String name);
}
