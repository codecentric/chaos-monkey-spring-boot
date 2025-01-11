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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

/**
 * @author Thorsten Deelmann, Dennis Effing
 */
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "chaos.monkey.assaults.kill-application-active=true", "spring.profiles.active=chaos-monkey"})
class KillAppAssaultIntegrationTest {

    @Autowired
    private KillAppAssault subject;

    @MockitoBean
    private KillAppAssault.ExitHelper exitHelper;

    @Test
    @DirtiesContext
    void killsSpringBootApplication() {
        subject.attack();

        await().untilAsserted(() -> verify(exitHelper).exitJvm(0));
    }
}
