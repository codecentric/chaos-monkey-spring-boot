/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChaosMonkeySettingsDto {

  ChaosMonkeyProperties chaosMonkeyProperties;
  AssaultPropertiesUpdate assaultProperties;
  WatcherProperties watcherProperties;

  public ChaosMonkeySettingsDto(
      @NotNull ChaosMonkeyProperties chaosMonkeyProperties,
      @NotNull AssaultProperties assaultProperties,
      @NotNull WatcherProperties watcherProperties) {
    this.chaosMonkeyProperties = chaosMonkeyProperties;
    this.assaultProperties = assaultProperties.toDto();
    this.watcherProperties = watcherProperties;
  }
}
