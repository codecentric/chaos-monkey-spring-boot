/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.chaos.monkey.chaosdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void changeAssaultConfiguration() {
        webTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk();
        webTestClient.post().uri("/actuator/chaosmonkey/assaults").contentType(MediaType.APPLICATION_JSON).bodyValue("""
                      {
                          "exceptionsActive": true,
                          "exception": {
                              "type": "java.lang.RuntimeException",
                              "method": "<init>",
                              "arguments": [
                                  {
                                      "type": "java.lang.String",
                                      "value": "Testing Chaos Monkey - RuntimeException"
                                  }
                              ]
                          }
                      }
                """).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Assault config has changed");
    }
}
