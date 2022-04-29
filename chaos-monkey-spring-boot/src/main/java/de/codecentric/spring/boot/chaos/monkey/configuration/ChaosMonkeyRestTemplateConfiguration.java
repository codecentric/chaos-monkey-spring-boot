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
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateCustomizer;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplatePostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateWatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "chaos.monkey.watcher", value = "rest-template", havingValue = "true")
@ConditionalOnClass(value = RestTemplate.class)
class ChaosMonkeyRestTemplateConfiguration {

    @Bean
    public ChaosMonkeyRestTemplatePostProcessor chaosMonkeyRestTemplatePostProcessor(final ChaosMonkeyRestTemplateCustomizer restTemplateCustomizer) {
        return new ChaosMonkeyRestTemplatePostProcessor(restTemplateCustomizer);
    }

    @Bean
    public ChaosMonkeyRestTemplateCustomizer chaosMonkeyRestTemplateCustomizer(final ChaosMonkeyRestTemplateWatcher chaosMonkeyRestTemplateWatcher) {
        return new ChaosMonkeyRestTemplateCustomizer(chaosMonkeyRestTemplateWatcher);
    }

    @Bean
    @DependsOn("chaosMonkeyRequestScope")
    public ChaosMonkeyRestTemplateWatcher chaosMonkeyRestTemplateInterceptor(final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
            final WatcherProperties watcherProperties, final AssaultProperties assaultProperties) {
        return new ChaosMonkeyRestTemplateWatcher(chaosMonkeyRequestScope, watcherProperties, assaultProperties);
    }
}
