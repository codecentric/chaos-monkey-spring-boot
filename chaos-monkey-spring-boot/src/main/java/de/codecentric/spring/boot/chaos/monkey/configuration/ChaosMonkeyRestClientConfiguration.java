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