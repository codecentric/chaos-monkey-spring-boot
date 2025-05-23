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
package com.example.chaos.monkey.chaosdemo.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(properties = {"chaos.monkey.assaults.exceptions-active=true", "chaos.monkey.watcher.repository=true",
        "chaos.monkey.assaults.level=1"})
class RepositoryWatcherIntegrationTest {

    @Autowired
    private HelloRepo defaultRepository;
    @Autowired
    private HelloRepoJpa jpaRepository;
    @Autowired
    private HelloRepoAnnotation repositoryDefinitionRepository;

    @Test
    void shouldAttackDefaultRepository() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(defaultRepository::count);
    }

    @Test
    void shouldAttackJpaRepository() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(jpaRepository::count);
    }

    @Test
    void shouldAttackRepositoryDefinitionRepository() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> repositoryDefinitionRepository.findById(0L));
    }
}
