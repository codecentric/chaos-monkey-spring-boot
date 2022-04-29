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
package com.example.chaos.monkey.chaosdemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoAnnotation;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoJpa;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoSearchAndSorting;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Benjamin Wilms */
@ExtendWith(MockitoExtension.class)
public class GreetingServiceTest {

    @Mock
    private HelloRepo helloRepoMock;

    @Mock
    private HelloRepoSearchAndSorting helloRepoSearchAndSortingMock;

    @Mock
    private HelloRepoJpa helloRepoJpaMock;

    @Mock
    private HelloRepoAnnotation helloRepoAnnotationMock;

    private GreetingService greetingService;

    @BeforeEach
    public void setUp() {
        greetingService = new GreetingService(helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
    }

    @Test
    public void greet() {
        assertThat(greetingService.greet()).isEqualTo("Greetings from the server side!");
    }

    @Test
    public void greetFromRepo() {
        String message = "message";
        Hello saveObject = new Hello(0, message);
        when(helloRepoMock.save(any(Hello.class))).thenReturn(saveObject);

        assertThat(greetingService.greetFromRepo()).isEqualTo(message);

        verify(helloRepoMock, times(1)).save(any(Hello.class));
        verifyNoMoreInteractions(helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
    }

    @Test
    public void testPagingAndSorting() {
        String message = "message";
        Hello saveObject = new Hello(0, message);
        when(helloRepoSearchAndSortingMock.save(any(Hello.class))).thenReturn(saveObject);
        when(helloRepoSearchAndSortingMock.findById(saveObject.getId())).thenReturn(Optional.of(saveObject));

        assertThat(greetingService.greetFromRepoPagingSorting()).isEqualTo(message);

        verify(helloRepoSearchAndSortingMock, times(1)).save(any(Hello.class));
        verify(helloRepoSearchAndSortingMock, times(1)).findById(saveObject.getId());
        verifyNoMoreInteractions(helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
    }

    @Test
    public void testJpaRepo() {
        String message = "message";
        Hello saveObject = new Hello(0, message);
        when(helloRepoJpaMock.save(any(Hello.class))).thenReturn(saveObject);
        when(helloRepoJpaMock.findById(saveObject.getId())).thenReturn(Optional.of(saveObject));

        assertThat(greetingService.greetFromRepoJpa()).isEqualTo(message);
        verify(helloRepoJpaMock, times(1)).save(any(Hello.class));
        verify(helloRepoJpaMock, times(1)).findById(saveObject.getId());
        verifyNoMoreInteractions(helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
    }

    @Test
    public void testAnnotationRepo() {
        String message = "message";
        Hello saveObject = new Hello(0, message);
        when(helloRepoAnnotationMock.save(any(Hello.class))).thenReturn(saveObject);
        when(helloRepoAnnotationMock.findById(saveObject.getId())).thenReturn(Optional.of(saveObject));

        assertThat(greetingService.greetFromRepoAnnotation()).isEqualTo(message);
        verify(helloRepoAnnotationMock, times(1)).save(any(Hello.class));
        verify(helloRepoAnnotationMock, times(1)).findById(saveObject.getId());
        verifyNoMoreInteractions(helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
    }
}
