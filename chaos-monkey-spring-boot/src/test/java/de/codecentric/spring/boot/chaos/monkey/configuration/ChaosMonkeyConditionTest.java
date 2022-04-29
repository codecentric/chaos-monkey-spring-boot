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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ChaosMonkeyConditionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(UserConfigurations.of(ChaosMonkeyConfiguration.class));

    @Test
    public void chaosmonkeyShouldBeUnload() {
        runner.run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isFalse());
    }

    @Test
    public void chaosmonkeyShouldBeloadedWithProfile() {
        runner.withSystemProperties("spring.profiles.active=chaos-monkey")
                .run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isTrue());
    }

    @Test
    public void chaosmonkeyShouldBeloadedWithProperty() {
        runner.withSystemProperties("LOAD_CHAOS_MONKEY=true").run(ctx -> assertThat(ctx.containsBean("chaosMonkeyRequestScope")).isTrue());
    }
}
