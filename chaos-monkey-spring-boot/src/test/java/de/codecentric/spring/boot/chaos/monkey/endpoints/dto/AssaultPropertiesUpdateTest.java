/*
 * Copyright 2025 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.endpoints.dto;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AssaultPropertiesUpdateTest {

    @Test
    void handlesDatabindException() {
        final AssaultProperties properties = new AssaultProperties();
        final AssaultPropertiesUpdate update = new AssaultPropertiesUpdate();
        try (final MockedConstruction<ObjectMapper> mockedConstruction = mockConstruction(ObjectMapper.class,
                (mock, context) -> when(mock.updateValue(properties, update)).thenThrow(DatabindException.class))) {
            assertThatThrownBy(() -> update.applyTo(properties)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
