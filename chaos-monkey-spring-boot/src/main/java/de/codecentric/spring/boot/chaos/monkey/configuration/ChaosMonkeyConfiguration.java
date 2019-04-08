/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.configuration;

import de.codecentric.spring.boot.chaos.monkey.assaults.*;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRuntimeScope;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.chaos.monkey.conditions.*;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyJmxEndpoint;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyRestEndpoint;
import de.codecentric.spring.boot.chaos.monkey.watcher.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Benjamin Wilms
 */
@Configuration
@Profile("chaos-monkey")
@EnableConfigurationProperties({ChaosMonkeyProperties.class, AssaultProperties.class, WatcherProperties.class})
@EnableScheduling
public class ChaosMonkeyConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyRequestScope.class);
    private final ChaosMonkeyProperties chaosMonkeyProperties;
    private final WatcherProperties watcherProperties;
    private final AssaultProperties assaultProperties;

    public ChaosMonkeyConfiguration(ChaosMonkeyProperties chaosMonkeyProperties, WatcherProperties watcherProperties,
                                    AssaultProperties assaultProperties) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
        this.watcherProperties = watcherProperties;
        this.assaultProperties = assaultProperties;

        try {
            String chaosLogo = StreamUtils.copyToString(new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset());
            LOGGER.info(chaosLogo);
        } catch (IOException e) {
            LOGGER.info("Chaos Monkey - ready to do evil");
        }

    }

    @Bean
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    public Metrics metrics() {
        return new Metrics();
    }


    @Bean
    public MetricEventPublisher publisher() {
        return new MetricEventPublisher();
    }

    @Bean
    public ChaosMonkeySettings settings() {
        return new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Bean
    public LatencyAssault latencyAssault() {
        return new LatencyAssault(settings(), publisher());
    }

    @Bean
    public ExceptionAssault exceptionAssault() {
        return new ExceptionAssault(settings(), publisher());
    }

    @Bean
    public KillAppAssault killAppAssault() {
        return new KillAppAssault(settings(), publisher());
    }

    @Bean
    public MemoryAssault memoryAssault() {
        return new MemoryAssault(Runtime.getRuntime(), settings(), publisher());
    }

    @Bean
    public ChaosMonkeyRequestScope chaosMonkeyRequestScope(List<ChaosMonkeyRequestAssault> chaosMonkeyAssaults, List<ChaosMonkeyAssault> allAssaults) {
        return new ChaosMonkeyRequestScope(settings(), chaosMonkeyAssaults, allAssaults, publisher());
    }

    @Bean
    public ChaosMonkeyRuntimeScope chaosMonkeyRuntimeScope(List<ChaosMonkeyRuntimeAssault> chaosMonkeyAssaults) {
        return new ChaosMonkeyRuntimeScope(settings(), chaosMonkeyAssaults);
    }

    @Bean
    @Conditional(AttackControllerCondition.class)
    public SpringControllerAspect controllerAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringControllerAspect(chaosMonkeyRequestScope, publisher());
    }

    @Bean
    @Conditional(AttackRestControllerCondition.class)
    public SpringRestControllerAspect restControllerAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringRestControllerAspect(chaosMonkeyRequestScope, publisher());
    }

    @Bean
    @Conditional(AttackServiceCondition.class)
    public SpringServiceAspect serviceAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringServiceAspect(chaosMonkeyRequestScope, publisher());
    }

    @Bean
    @Conditional(AttackComponentCondition.class)
    public SpringComponentAspect componentAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringComponentAspect(chaosMonkeyRequestScope, publisher());
    }

    @Bean
    @Conditional(AttackRepositoryCondition.class)
    public SpringRepositoryAspect repositoryAspect(ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
        return new SpringRepositoryAspect(chaosMonkeyRequestScope, publisher());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyRestEndpoint chaosMonkeyRestEndpoint(ChaosMonkeyRuntimeScope runtimeScope) {
        return new ChaosMonkeyRestEndpoint(settings(), runtimeScope);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint() {
        return new ChaosMonkeyJmxEndpoint(settings());
    }

}