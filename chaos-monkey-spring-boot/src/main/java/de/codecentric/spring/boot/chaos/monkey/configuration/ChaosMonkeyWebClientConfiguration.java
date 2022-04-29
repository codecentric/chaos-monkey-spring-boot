/*
 * Copyright 2021-2022 the original author or authors.
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
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientCustomizer;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientPostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientWatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "chaos.monkey.watcher", value = "web-client", havingValue = "true")
@ConditionalOnClass(value = WebClient.class)
class ChaosMonkeyWebClientConfiguration {

    @Bean
    public ChaosMonkeyWebClientPostProcessor chaosMonkeyWebClientPostProcessor(final ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher) {
        return new ChaosMonkeyWebClientPostProcessor(chaosMonkeyWebClientWatcher);
    }

    @Bean
    public ChaosMonkeyWebClientCustomizer chaosMonkeyWebClientCustomizer(final ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher) {
        return new ChaosMonkeyWebClientCustomizer(chaosMonkeyWebClientWatcher);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public ChaosMonkeyWebClientWatcher chaosMonkeyWebClientWatcher(final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
            final WatcherProperties watcherProperties, final AssaultProperties assaultProperties) {
        return new ChaosMonkeyWebClientWatcher(chaosMonkeyRequestScope, watcherProperties, assaultProperties);
    }
}
