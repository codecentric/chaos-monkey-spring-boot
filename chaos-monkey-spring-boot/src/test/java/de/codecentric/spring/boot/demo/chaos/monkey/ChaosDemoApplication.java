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

package de.codecentric.spring.boot.demo.chaos.monkey;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication.TestRestTemplateConfigurationProperties;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/** @author Benjamin Wilms */
@SpringBootApplication
@EnableConfigurationProperties(value = {TestRestTemplateConfigurationProperties.class})
public class ChaosDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChaosDemoApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate(final TestRestTemplateConfigurationProperties properties) {
    return new RestTemplateBuilder()
        .setReadTimeout(Duration.of(properties.timeOut, ChronoUnit.MILLIS))
        .build();
  }

  @Data
  @ConfigurationProperties(prefix = "chaos.monkey.test.rest-template")
  static class TestRestTemplateConfigurationProperties {
    private Long timeOut = 10000L;
  }
}
