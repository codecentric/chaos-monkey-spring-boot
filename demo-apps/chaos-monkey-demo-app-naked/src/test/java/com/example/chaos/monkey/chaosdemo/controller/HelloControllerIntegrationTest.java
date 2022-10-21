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
package com.example.chaos.monkey.chaosdemo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.chaos.monkey.chaosdemo.ChaosDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

/** @author Benjamin Wilms */
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class HelloControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private HelloController helloController;

    @Test
    public void contextLoads() {
        assertThat(helloController).isNotNull();
    }

    @Test
    public void checkHelloEndpoint() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + this.serverPort + "/hello", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello!", response.getBody());
    }

    @Test
    public void checkGoodbyeEndpoint() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + this.serverPort + "/goodbye", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goodbye!", response.getBody());
    }
}
