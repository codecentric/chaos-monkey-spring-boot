/*
 * Copyright 2018-2025 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** @author Benjamin Wilms */
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test-default-profile.properties")
@EnableConfigurationProperties({AssaultProperties.class, WatcherProperties.class})
class ChaosDemoApplicationDefaultProfileTest {
    @Autowired
    private AssaultProperties assaultProperties;

    @Autowired
    private WatcherProperties watcherProperties;

    @Test
    void checkEnvWatcherController() {
        assertTrue(watcherProperties.isController());
    }

    @Test
    void checkEnvAssaultLatencyRange() {
        assertAll(() -> assertEquals(100, assaultProperties.getLatencyRangeStart()), () -> assertEquals(200, assaultProperties.getLatencyRangeEnd()));
    }

    @Test
    void checkEnvCustomServiceWatcherList() {
        assertEquals(List.of("com.example.chaos.monkey.chaosdemo.controller.HelloController.sayHello",
                "com.example.chaos.monkey.chaosdemo.controller.HelloController.sayGoodbye"), assaultProperties.getWatchedCustomServices());
    }
}
