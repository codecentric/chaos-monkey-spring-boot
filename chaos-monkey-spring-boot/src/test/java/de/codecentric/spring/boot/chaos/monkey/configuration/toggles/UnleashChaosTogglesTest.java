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
package de.codecentric.spring.boot.chaos.monkey.configuration.toggles;

import static org.junit.jupiter.api.Assertions.*;

import io.getunleash.FakeUnleash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnleashChaosTogglesTest {
    private UnleashChaosToggles sut;
    private FakeUnleash fakeUnleash;

    @BeforeEach
    public void setup() {
        fakeUnleash = new FakeUnleash();
        sut = new UnleashChaosToggles(fakeUnleash);
    }

    @Test
    public void unleashTogglesAreDisabledByDefault() {
        assertFalse(sut.isEnabled("chaos.monkey.repository"));
    }

    @Test
    public void unleashTogglesThatAreEnabledAlsoEnableTheChaosToggle() {
        fakeUnleash.enable("chaos.monkey.repository");
        assertTrue(sut.isEnabled("chaos.monkey.repository"));
    }

    @Test
    public void unleashTogglesThatAreEnabledThatDontMatchTheChaosToggleAUnaffected() {
        fakeUnleash.enable("chaos.monkey.controller");
        assertFalse(sut.isEnabled("chaos.monkey.repository"));
    }
}
