/*
 * Copyright 2023 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.watcher;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.repository.DemoRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(properties = {"chaos.monkey.watcher.repository=true", "chaos.monkey.assaults.exceptions-active=true",
        "chaos.monkey.assaults.deterministic=true","chaos.monkey.assaults.level=2",
        "chaos.monkey.enabled=true"}, classes = {ChaosDemoApplication.class})
@ActiveProfiles("chaos-monkey")
public class ChaosMonkeyRepositoryWatcherIntegrationMultipleInvocationTest {

    private static final Logger log = LoggerFactory.getLogger(ChaosMonkeyRepositoryWatcherIntegrationMultipleInvocationTest.class);
    @Autowired
    private DemoRepository demoRepository;

    @Test
    public void testIfCrudRepositoryCallThrowsExceptionExpectedBehavior() {
        for (int invocationCount = 0; invocationCount < 6; invocationCount++) {
            log.info("testIfCrudRepositoryCallThrowsExceptionExpectedBehavior InvocationCount# {}",invocationCount);
            if(invocationCount % 2 == 0) {
                assertThatExceptionOfType(RuntimeException.class).isThrownBy(demoRepository::count);
            } else {
                assertThatNoException().isThrownBy(demoRepository::count);
            }
        }
    }

    @Test
    public void testIfCrudRepositoryCallThrowsExceptionActualBehavior() {
        for (int invocationCount = 0; invocationCount < 6; invocationCount++) {
            log.info("testIfCrudRepositoryCallThrowsExceptionActualBehavior InvocationCount# {}",invocationCount);
            if(invocationCount % 2 == 0) {
                assertThatExceptionOfType(RuntimeException.class).isThrownBy(demoRepository::count);
            } else {
                assertThatExceptionOfType(RuntimeException.class).isThrownBy(demoRepository::count);
            }
        }
    }


}