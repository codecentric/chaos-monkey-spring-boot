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
 *
 */

package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.sun.management.OperatingSystemMXBean;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRequestAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.CpuAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.MemoryAssault;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRuntimeScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyScheduler;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggles;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyJmxEndpoint;
import de.codecentric.spring.boot.chaos.monkey.endpoints.ChaosMonkeyRestEndpoint;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyBeanPostProcessor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyDefaultAdvice;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.ChaosMonkeyHealthIndicatorAdvice;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyAnnotationPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.advisor.ChaosMonkeyPointcutAdvisor;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.ChaosMonkeyBaseClassFilter;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.MethodNameFilter;
import de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter.SpringHookMethodsFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.List;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Conditional(ChaosMonkeyCondition.class)
@EnableConfigurationProperties({
  ChaosMonkeyProperties.class,
  AssaultProperties.class,
  WatcherProperties.class
})
@Import({
  UnleashChaosConfiguration.class,
  ChaosMonkeyWebClientConfiguration.class,
  ChaosMonkeyRestTemplateConfiguration.class
})
@EnableScheduling
public class ChaosMonkeyConfiguration {

  private static final Logger Logger = LoggerFactory.getLogger(ChaosMonkeyConfiguration.class);

  private static final String CHAOS_MONKEY_TASK_SCHEDULER = "chaosMonkeyTaskScheduler";

  private final ChaosMonkeyProperties chaosMonkeyProperties;

  private final WatcherProperties watcherProperties;

  private final AssaultProperties assaultProperties;

  public ChaosMonkeyConfiguration(
      ChaosMonkeyProperties chaosMonkeyProperties,
      WatcherProperties watcherProperties,
      AssaultProperties assaultProperties) {
    this.chaosMonkeyProperties = chaosMonkeyProperties;
    this.watcherProperties = watcherProperties;
    this.assaultProperties = assaultProperties;

    try {
      String chaosLogo =
          StreamUtils.copyToString(
              new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset());
      Logger.info(chaosLogo);
    } catch (IOException e) {
      Logger.info("Chaos Monkey - ready to do evil");
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
  public CpuAssault cpuAssault() {
    return new CpuAssault(
        ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class), settings(), publisher());
  }

  @Bean
  public ChaosMonkeyRequestScope chaosMonkeyRequestScope(
      List<ChaosMonkeyRequestAssault> chaosMonkeyAssaults,
      List<ChaosMonkeyAssault> allAssaults,
      ChaosToggles chaosToggles,
      ChaosToggleNameMapper chaosToggleNameMapper) {
    return new ChaosMonkeyRequestScope(
        settings(),
        chaosMonkeyAssaults,
        allAssaults,
        publisher(),
        chaosToggles,
        chaosToggleNameMapper);
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
  public ChaosMonkeyScheduler chaosMonkeyScheduler(
      @Qualifier(CHAOS_MONKEY_TASK_SCHEDULER) TaskScheduler scheduler,
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
  public ChaosMonkeyRuntimeScope chaosMonkeyRuntimeScope(
      List<ChaosMonkeyRuntimeAssault> chaosMonkeyAssaults) {
    return new ChaosMonkeyRuntimeScope(settings(), chaosMonkeyAssaults);
  }

  @Bean
  public ChaosMonkeyBaseClassFilter chaosMonkeyBaseClassFilter() {
    return new ChaosMonkeyBaseClassFilter(watcherProperties);
  }

  @Bean
  public ChaosMonkeyPointcutAdvisor controllerPointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    return new ChaosMonkeyAnnotationPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.CONTROLLER),
        Controller.class);
  }

  @Bean
  public ChaosMonkeyPointcutAdvisor restControllerPointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    return new ChaosMonkeyAnnotationPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.REST_CONTROLLER),
        RestController.class);
  }

  @Bean
  public ChaosMonkeyPointcutAdvisor servicePointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    return new ChaosMonkeyAnnotationPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.SERVICE),
        Service.class,
        SpringHookMethodsFilter.INSTANCE);
  }

  @Bean
  public ChaosMonkeyPointcutAdvisor componentPointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    return new ChaosMonkeyAnnotationPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.COMPONENT),
        Component.class,
        SpringHookMethodsFilter.INSTANCE);
  }

  @Bean
  @ConditionalOnClass(name = "org.springframework.data.repository.Repository")
  public ChaosMonkeyPointcutAdvisor jpaRepositoryPointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher)
      throws ClassNotFoundException {
    @SuppressWarnings("unchecked")
    val repositoryDefinition =
        (Class<? extends Annotation>)
            Class.forName("org.springframework.data.repository.RepositoryDefinition");
    Class<?> repository = Class.forName("org.springframework.data.repository.Repository");
    return new ChaosMonkeyPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.REPOSITORY),
        ClassFilters.union(
            new AnnotationClassFilter(repositoryDefinition, false),
            new RootClassFilter(repository)),
        SpringHookMethodsFilter.INSTANCE);
  }

  @Bean
  public ChaosMonkeyPointcutAdvisor jdbcRepositoryPointcutAdvisor(
      ChaosMonkeyBaseClassFilter baseClassFilter,
      ChaosMonkeyRequestScope requestScope,
      MetricEventPublisher eventPublisher) {
    return new ChaosMonkeyAnnotationPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyDefaultAdvice(
            requestScope, eventPublisher, watcherProperties, ChaosTarget.REPOSITORY),
        Repository.class);
  }

  @Bean
  @ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
  public ChaosMonkeyPointcutAdvisor healthIndicatorAdviceProvider(
      ChaosMonkeyBaseClassFilter baseClassFilter, ChaosMonkeyRequestScope requestScope)
      throws ClassNotFoundException {
    Class<?> healthIndicatorClass =
        Class.forName("org.springframework.boot.actuate.health.HealthIndicator");
    return new ChaosMonkeyPointcutAdvisor(
        baseClassFilter,
        new ChaosMonkeyHealthIndicatorAdvice(requestScope, watcherProperties),
        new RootClassFilter(healthIndicatorClass),
        new MethodNameFilter("getHealth"));
  }

  @Bean
  @DependsOn("chaosMonkeyRequestScope")
  public ChaosMonkeyBeanPostProcessor chaosMonkeyBeanPostProcessor(
      ChaosMonkeyRequestScope chaosMonkeyRequestScope) {
    return new ChaosMonkeyBeanPostProcessor(
        watcherProperties, chaosMonkeyRequestScope, publisher());
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnAvailableEndpoint
  public ChaosMonkeyRestEndpoint chaosMonkeyRestEndpoint(
      ChaosMonkeyRuntimeScope runtimeScope, ChaosMonkeyScheduler scheduler) {
    return new ChaosMonkeyRestEndpoint(settings(), runtimeScope, scheduler);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnAvailableEndpoint
  public ChaosMonkeyJmxEndpoint chaosMonkeyJmxEndpoint() {
    return new ChaosMonkeyJmxEndpoint(settings());
  }
}
