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

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.chaos.monkey.conditions.*;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyJmxEndpoint;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyRestEndpoint;
import de.codecentric.spring.boot.chaos.monkey.watcher.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
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
public class ChaosMonkeyConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);
    private final ChaosMonkeyProperties chaosMonkeyProperties;
    private final WatcherProperties watcherProperties;
    private final AssaultProperties assaultProperties;
    private final MeterRegistry meterRegistry;


    public ChaosMonkeyConfiguration(ChaosMonkeyProperties chaosMonkeyProperties, WatcherProperties watcherProperties,
                                    AssaultProperties assaultProperties, MeterRegistry meterRegistry) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
        this.watcherProperties = watcherProperties;
        this.assaultProperties = assaultProperties;
        this.meterRegistry = meterRegistry;

        try {
            String chaosLogo = StreamUtils.copyToString(new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset());
            LOGGER.info(chaosLogo);
        } catch (IOException e) {
            LOGGER.info("Chaos Monkey - ready to do evil");
        }

    }

    @Bean
    public ChaosMonkeySettings settings() {
        return new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Bean
    public LatencyAssault latencyAssault() {
        return new LatencyAssault(settings(), metrics(meterRegistry));
    }

    @Bean
    public ExceptionAssault exceptionAssault() {
        return new ExceptionAssault(settings(), metrics(meterRegistry));
    }

    @Bean
    public KillAppAssault killAppAssault() {
        return new KillAppAssault(settings(), metrics(meterRegistry));
    }

    @Bean
    public ChaosMonkey chaosMonkey(List<ChaosMonkeyAssault> chaosMonkeyAssaults) {
        return new ChaosMonkey(settings(), chaosMonkeyAssaults, metrics(meterRegistry));
    }

    @Bean
    @Conditional(AttackControllerCondition.class)
    public SpringControllerAspect controllerAspect(ChaosMonkey chaosMonkey) {
        return new SpringControllerAspect(chaosMonkey, metrics(meterRegistry));
    }

    @Bean
    @Conditional(AttackRestControllerCondition.class)
    public SpringRestControllerAspect restControllerAspect(ChaosMonkey chaosMonkey) {
        return new SpringRestControllerAspect(chaosMonkey, metrics(meterRegistry));
    }

    @Bean
    @Conditional(AttackServiceCondition.class)
    public SpringServiceAspect serviceAspect(ChaosMonkey chaosMonkey) {
        return new SpringServiceAspect(chaosMonkey);
    }

    @Bean
    @Conditional(AttackComponentCondition.class)
    public SpringComponentAspect componentAspect(ChaosMonkey chaosMonkey) {
        return new SpringComponentAspect(chaosMonkey);
    }

    @Bean
    @Conditional(AttackRepositoryCondition.class)
    public SpringRepositoryAspect repositoryAspect(ChaosMonkey chaosMonkey) {
        return new SpringRepositoryAspect(chaosMonkey);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyRestEndpoint chaosMonkeyRestEndpoint() {
        return new ChaosMonkeyRestEndpoint(settings());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint() {
        return new ChaosMonkeyJmxEndpoint(settings());
    }

    @Bean
    public Metrics metrics(MeterRegistry meterRegistry) {
        return new Metrics(meterRegistry);
    }

}
