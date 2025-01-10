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
package com.example.chaos.monkey.chaosdemo.controller;

import com.example.chaos.monkey.chaosdemo.ChaosDemoApplication;
import com.example.chaos.monkey.chaosdemo.retry.RetryComponent;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-component-retry.properties")
public class RetryComponentIntegrationTest {

    @Autowired
    private RetryComponent retryComponent;

    @Autowired
    private ChaosMonkeyConfiguration chaosMonkeyConfiguration;

    @Test
    public void callingPublicMethodOnComponent() {
        assertTrue(chaosMonkeyConfiguration.chaosMonkeySettings().getWatcherProperties().isComponent());

        assertEquals("Hello from Recover: Chaos Monkey - RuntimeException", retryComponent.sayHello());
    }
}
