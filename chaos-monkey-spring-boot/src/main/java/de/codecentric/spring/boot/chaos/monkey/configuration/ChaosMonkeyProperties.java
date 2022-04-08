/*
 * Copyright 2018 the original author or authors.
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
 */

package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey")
public class ChaosMonkeyProperties {

  private boolean enabled = false;

  @Nullable
  @Setter(AccessLevel.NONE)
  private Long lastEnabledToggleTimestamp = null;

  private String togglePrefix = "chaos.monkey";

  @Nullable
  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      lastEnabledToggleTimestamp = System.currentTimeMillis();
      this.enabled = enabled;
    }
  }
}
