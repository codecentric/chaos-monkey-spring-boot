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
package com.example.chaos.monkey.toggledemo;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.ChaosToggleNameMapper;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ChaosDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaosDemoApplication.class, args);
    }

    @Bean
    public UnleashContextProvider unleashContextProvider() {
        return () -> {
            UnleashContext.Builder context = new UnleashContext.Builder();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                context.userId(((User) auth.getPrincipal()).getUsername());
            }

            return context.build();
        };
    }

    @Bean
    public Unleash unleash(UnleashContextProvider contextProvider) {
        UserAwareFakeUnleash unleash = new UserAwareFakeUnleash(contextProvider);

        // The following line is commented out, but demonstrates how you can enable a
        // toggle with
        // the demo app without running an actual Unleash server
        // unleash.enable("chaos.monkey.controller");
        return unleash;
    }

    @Bean
    public ChaosToggleNameMapper myChaosToggles(ChaosMonkeyProperties chaosMonkeyProperties) {
        return new MyAppToggleMapper(chaosMonkeyProperties.getTogglePrefix());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
