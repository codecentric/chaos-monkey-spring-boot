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

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultChaosToggleNameMapperTest {

    private DefaultChaosToggleNameMapper sut;

    @BeforeEach
    public void setup() {
        sut = new DefaultChaosToggleNameMapper("toggle.prefix");
    }

    @Test
    public void chaosTypeCanBeNull() {
        assertEquals(sut.mapName(null, "com.example.MyController.hello"), "toggle.prefix.unknown");
    }

    @Test
    public void chaosTypeNameIsUsedAsSuffix() {
        assertEquals(sut.mapName(ChaosTarget.REPOSITORY, "com.example.MyController.hello"), "toggle.prefix.repository");
    }
}
