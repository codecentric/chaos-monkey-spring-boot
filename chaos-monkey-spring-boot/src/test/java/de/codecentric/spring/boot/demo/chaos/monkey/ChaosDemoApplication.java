/*
 * Copyright 2018-2022 the original author or authors.
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
package de.codecentric.spring.boot.demo.chaos.monkey;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication.TestOutgoingConfigurationProperties;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;

/** @author Benjamin Wilms */
@SpringBootApplication
@EnableConfigurationProperties(value = {TestOutgoingConfigurationProperties.class})
public class ChaosDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaosDemoApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplateWithTimeout(final TestOutgoingConfigurationProperties properties, final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.setReadTimeout(Duration.of(properties.timeOut, ChronoUnit.MILLIS)).build();
    }

    @Bean
    public WebClient webClient(final TestOutgoingConfigurationProperties properties, final Builder webClientBuilder) {
        HttpClient client = HttpClient.create().responseTimeout(Duration.ofMillis(properties.timeOut));
        return webClientBuilder.clientConnector(new ReactorClientHttpConnector(client)).build();
    }

    @Data
    @ConfigurationProperties(prefix = "chaos.monkey.test.rest-template")
    static class TestOutgoingConfigurationProperties {

        private Long timeOut = 10000L;
    }
}
