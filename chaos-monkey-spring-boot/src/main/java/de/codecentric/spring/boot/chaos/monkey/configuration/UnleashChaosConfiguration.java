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

import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.UnleashChaosToggles;
import io.getunleash.Unleash;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Unleash.class)
@ConditionalOnProperty(value = "chaos.monkey.toggle.unleash.enabled")
public class UnleashChaosConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Unleash.class)
    public ChaosToggles unleashChaosToggles(Unleash unleash) {
        return new UnleashChaosToggles(unleash);
    }
}
