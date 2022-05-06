/*
 * Copyright 2018-2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** @author Thorsten Deelmann */
public class KillAppAssault implements ChaosMonkeyRuntimeAssault, ApplicationContextAware {

    private static final Logger Logger = LoggerFactory.getLogger(KillAppAssault.class);

    private final ChaosMonkeySettings settings;

    private ApplicationContext context;

    private MetricEventPublisher metricEventPublisher;

    public KillAppAssault(ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isKillApplicationActive();
    }

    @Override
    public void attack() {
        try {
            Logger.info("Chaos Monkey - I am killing your Application!");

            if (metricEventPublisher != null) {
                metricEventPublisher.publishMetricEvent(MetricType.KILLAPP_ASSAULT);
            }

            int exit = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);

            long remaining = 5000;
            long end = System.currentTimeMillis() + remaining;
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(remaining); // wait before kill to deliver some metrics
                    break;
                } catch (InterruptedException ignored) {
                    remaining = end - System.currentTimeMillis();
                }
            }

            System.exit(exit);
        } catch (Exception e) {
            Logger.info("Chaos Monkey - Unable to kill the App, I am not the BOSS!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public String getCronExpression(AssaultProperties assaultProperties) {
        return assaultProperties.getKillApplicationCronExpression() != null
                ? assaultProperties.getKillApplicationCronExpression()
                : assaultProperties.getRuntimeAssaultCronExpression();
    }
}
