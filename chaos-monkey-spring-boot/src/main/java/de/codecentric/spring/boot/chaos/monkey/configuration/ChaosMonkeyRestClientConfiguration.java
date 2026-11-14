/*
 * Copyright 2026 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestClientCustomizer;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestClientPostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestClientWatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnClass(value = RestClient.class)
class ChaosMonkeyRestClientConfiguration {

    @Bean
    public ChaosMonkeyRestClientPostProcessor chaosMonkeyRestClientPostProcessor(final ChaosMonkeyRestClientWatcher chaosMonkeyRestClientWatcher) {
        return new ChaosMonkeyRestClientPostProcessor(chaosMonkeyRestClientWatcher);
    }

    @Bean
    public ChaosMonkeyRestClientCustomizer chaosMonkeyRestClientCustomizer(final ChaosMonkeyRestClientWatcher chaosMonkeyRestClientWatcher) {
        return new ChaosMonkeyRestClientCustomizer(chaosMonkeyRestClientWatcher);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public ChaosMonkeyRestClientWatcher chaosMonkeyRestClientWatcher(final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
            final WatcherProperties watcherProperties) {
        return new ChaosMonkeyRestClientWatcher(chaosMonkeyRequestScope, watcherProperties);
    }
}