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
package com.example.chaos.monkey.chaosdemo.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** @author Benjamin Wilms */
public class HelloTest {

    @Test
    public void checkConstructorGetter() {
        String expectedValueMessage = "test";
        long id = 0;
        Hello helloToTest = new Hello(id, expectedValueMessage);

        assertThat(helloToTest.getMessage()).isEqualTo(expectedValueMessage);
        assertThat(helloToTest.getId()).isEqualTo(id);
    }

    @Test
    public void checkSetterGetter() {
        String expectedValueMessage = "test";
        long id = 0;
        Hello helloToTest = new Hello(99, "empty");
        helloToTest.setId(id);
        helloToTest.setMessage(expectedValueMessage);

        assertThat(helloToTest.getMessage()).isEqualTo(expectedValueMessage);
        assertThat(helloToTest.getId()).isEqualTo(id);
    }
}
