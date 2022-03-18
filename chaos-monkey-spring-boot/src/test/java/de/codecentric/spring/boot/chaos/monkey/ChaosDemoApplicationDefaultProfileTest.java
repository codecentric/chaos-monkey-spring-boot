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
 *
 */

package de.codecentric.spring.boot.chaos.monkey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

/** @author Benjamin Wilms */
@SpringBootTest(
    classes = ChaosDemoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test-default-profile.properties")
class ChaosDemoApplicationDefaultProfileTest {

  @Autowired(required = false)
  private ChaosMonkeyRequestScope chaosMonkeyRequestScope;

  @Autowired private Environment env;

  @Test
  void contextLoads() {
    assertNull(chaosMonkeyRequestScope);
  }

  @Test
  void checkEnvWatcherController() {
    assertThat(env.getProperty("chaos.monkey.watcher.controller")).isEqualTo("true");
  }

  @Test
  void checkEnvAssaultLatencyRangeStart() {
    assertThat(env.getProperty("chaos.monkey.assaults.latency-range-start")).isEqualTo("100");
  }

  @Test
  void checkEnvAssaultLatencyRangeEnd() {
    assertThat(env.getProperty("chaos.monkey.assaults.latency-range-end")).isEqualTo("200");
  }

  @Test
  void checkEnvCustomServiceWatcherList() {
    List<String> stringList =
        env.getProperty("chaos.monkey.assaults.watchedCustomServices", List.class);
    assertThat(stringList).hasSize(2);
    assertThat(stringList.get(0))
        .isEqualTo("com.example.chaos.monkey.chaosdemo.controller.HelloController.sayHello");
    assertThat(stringList.get(1))
        .isEqualTo("com.example.chaos.monkey.chaosdemo.controller.HelloController.sayGoodbye");
  }
}
