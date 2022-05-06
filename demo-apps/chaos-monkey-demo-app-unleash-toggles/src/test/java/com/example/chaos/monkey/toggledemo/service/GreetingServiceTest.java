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
package com.example.chaos.monkey.toggledemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.chaos.monkey.toggledemo.repo.SimpleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Clint Checketts */
@ExtendWith(MockitoExtension.class)
public class GreetingServiceTest {

    @Mock
    private SimpleRepository helloRepoMock;

    private GreetingService greetingService;

    @BeforeEach
    public void setUp() {
        greetingService = new GreetingService(helloRepoMock);
    }

    @Test
    public void greet() {
        assertThat(greetingService.greet()).isEqualTo("Greetings from the server side!");
    }

    @Test
    public void greetFromRepo() {
        String message = "message";
        when(helloRepoMock.getGreeting()).thenReturn(message);

        assertThat(greetingService.greetFromRepo()).isEqualTo(message);

        verify(helloRepoMock, times(1)).getGreeting();
        verifyNoMoreInteractions(helloRepoMock);
    }
}
