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
import de.codecentric.spring.boot.demo.chaos.monkey.repository.CrudDemoRepository;
import de.codecentric.spring.boot.demo.chaos.monkey.repository.DemoRepository;
import de.codecentric.spring.boot.demo.chaos.monkey.repository.DemoRepositoryJDBC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@SpringBootTest(properties = {"chaos.monkey.watcher.repository=true", "chaos.monkey.assaults.exceptions-active=true",
        "chaos.monkey.enabled=true"}, classes = {ChaosDemoApplication.class})
@ActiveProfiles("chaos-monkey")
public class ChaosMonkeyRepositoryWatcherIntegrationTest {

    @Autowired
    private CrudDemoRepository crudDemoRepository;

    @Autowired
    private DemoRepository demoRepository;

    @Autowired
    private DemoRepositoryJDBC demoRepositoryJDBC;

    @Test
    public void testIfCrudRepositoryCallThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(crudDemoRepository::count);
    }

    @Test
    public void testIfDemoRepositoryCallThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(demoRepository::count);
    }

    @Test
    public void testIfDemoRepositoryJDBCCallThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(demoRepositoryJDBC::sayHello);
    }
}