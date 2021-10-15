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

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** @author Benjamin Wilms */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.watcher")
public class WatcherProperties {

  private boolean controller = false;

  private boolean restController = false;

  private boolean service = false;

  private boolean repository = false;

  private boolean component = false;

  private boolean restTemplate = false;

  private boolean webClient = false;

  private boolean actuatorHealth = false;

  private List<String> beans = Collections.emptyList();
}
