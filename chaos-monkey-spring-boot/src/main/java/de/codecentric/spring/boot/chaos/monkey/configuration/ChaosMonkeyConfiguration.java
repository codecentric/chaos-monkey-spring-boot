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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.sun.management.OperatingSystemMXBean;
import de.codecentric.spring.boot.chaos.monkey.assaults.*;
import de.codecentric.spring.boot.chaos.monkey.component.*;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyJmxEndpoint;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyRestEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.retry.annotation.Recover;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.List;

@AutoConfiguration
@Conditional(ChaosMonkeyCondition.class)
@EnableConfigurationProperties({ChaosMonkeyProperties.class, AssaultProperties.class, WatcherProperties.class})
@Import({UnleashChaosConfiguration.class, ChaosMonkeyWebClientConfiguration.class, ChaosMonkeyRestTemplateConfiguration.class,
        ChaosMonkeyAdvisorConfiguration.class, ChaosMonkeyOpenApiConfiguration.class})
@EnableScheduling
@Slf4j
public class ChaosMonkeyConfiguration {

    private static final String CHAOS_MONKEY_TASK_SCHEDULER = "chaosMonkeyTaskScheduler";

    private final ChaosMonkeyProperties chaosMonkeyProperties;

    private final WatcherProperties watcherProperties;

    private final AssaultProperties assaultProperties;

    public ChaosMonkeyConfiguration(ChaosMonkeyProperties chaosMonkeyProperties, WatcherProperties watcherProperties,
            AssaultProperties assaultProperties) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
        this.watcherProperties = watcherProperties;
        this.assaultProperties = assaultProperties;

        try {
            log.info(StreamUtils.copyToString(new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset()));
        } catch (IOException e) {
            log.info("Chaos Monkey - ready to do evil");
        }
    }

    @Bean
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    public Metrics chaosMonkeyMetrics() {
        return new Metrics();
    }

    @Bean
    public MetricEventPublisher chaosMonkeyMetricsPublisher() {
        return new MetricEventPublisher();
    }

    @Bean
    public ChaosMonkeySettings chaosMonkeySettings() {
        return new ChaosMonkeySettings(chaosMonkeyProperties, assaultProperties, watcherProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LatencyAssault latencyAssault(ChaosMonkeySettings settings, MetricEventPublisher publisher) {
        return new LatencyAssault(settings, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionAssault exceptionAssault(ChaosMonkeySettings settings, MetricEventPublisher publisher) {
        return new ExceptionAssault(settings, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public KillAppAssault killAppAssault(ChaosMonkeySettings settings, MetricEventPublisher publisher) {
        return new KillAppAssault(settings, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public MemoryAssault memoryAssault(ChaosMonkeySettings settings, MetricEventPublisher publisher) {
        return new MemoryAssault(Runtime.getRuntime(), settings, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public CpuAssault cpuAssault(ChaosMonkeySettings settings, MetricEventPublisher publisher) {
        return new CpuAssault(ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class), settings, publisher);
    }

    @Bean
    public ChaosMonkeyRequestScope chaosMonkeyRequestScope(List<ChaosMonkeyRequestAssault> chaosMonkeyAssaults, List<ChaosMonkeyAssault> allAssaults,
            ChaosToggles chaosToggles, ChaosToggleNameMapper chaosToggleNameMapper, ChaosMonkeySettings settings, MetricEventPublisher publisher, MethodFilter methodFilter) {
        return new ChaosMonkeyRequestScope(settings, chaosMonkeyAssaults, allAssaults, publisher, chaosToggles, chaosToggleNameMapper, methodFilter);
    }

    @Bean
    @ConditionalOnMissingBean(ChaosToggleNameMapper.class)
    public ChaosToggleNameMapper chaosToggleNameMapper(ChaosMonkeyProperties chaosMonkeyProperties) {
        return new DefaultChaosToggleNameMapper(chaosMonkeyProperties.getTogglePrefix());
    }

    @Bean
    @ConditionalOnMissingBean(ChaosToggles.class)
    public ChaosToggles chaosToggles() {
        return new DefaultChaosToggles();
    }

    @Bean
    public ChaosMonkeyScheduler chaosMonkeyScheduler(@Qualifier(CHAOS_MONKEY_TASK_SCHEDULER) TaskScheduler scheduler,
            List<ChaosMonkeyRuntimeAssault> assaults) {
        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        registrar.setTaskScheduler(scheduler);
        return new ChaosMonkeyScheduler(registrar, assaultProperties, assaults);
    }

    @Bean(name = CHAOS_MONKEY_TASK_SCHEDULER)
    public TaskScheduler chaosMonkeyTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public ChaosMonkeyRuntimeScope chaosMonkeyRuntimeScope(ChaosMonkeySettings settings, List<ChaosMonkeyRuntimeAssault> chaosMonkeyAssaults) {
        return new ChaosMonkeyRuntimeScope(settings, chaosMonkeyAssaults);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ChaosMonkeyRestEndpoint chaosMonkeyRestEndpoint(ChaosMonkeySettings settings, ChaosMonkeyRuntimeScope runtimeScope,
            ChaosMonkeyScheduler scheduler) {
        return new ChaosMonkeyRestEndpoint(settings, runtimeScope, scheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint(ChaosMonkeySettings settings) {
        return new ChaosMonkeyJmxEndpoint(settings);
    }

    @Bean
    @ConditionalOnClass(Recover.class)
    public MethodFilter recoverMethodFilter() {
        return new RecoverMethodFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MethodFilter methodFilter() {
        return (target, method) -> false;
    }
}
