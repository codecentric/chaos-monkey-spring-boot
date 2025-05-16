/*
 * Copyright 2023-2025 the original author or authors.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

@SpringBootTest(properties = {"chaos.monkey.watcher.repository=true", "chaos.monkey.assaults.exceptions-active=true",
        "chaos.monkey.assaults.deterministic=true", "chaos.monkey.assaults.level=2",
        "chaos.monkey.enabled=true"}, classes = {ChaosDemoApplication.class})
@ActiveProfiles("chaos-monkey")
public class ChaosMonkeyDeterministicRepositoryWatcherIntegrationTest {

    @Autowired
    private DemoRepository demoRepository;

    @Test
    public void shouldThrowExceptionOnEverySecondCall() {
        assertThatNoException().isThrownBy(() -> demoRepository.count());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> demoRepository.count());
        assertThatNoException().isThrownBy(() -> demoRepository.count());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> demoRepository.count());
    }
}
