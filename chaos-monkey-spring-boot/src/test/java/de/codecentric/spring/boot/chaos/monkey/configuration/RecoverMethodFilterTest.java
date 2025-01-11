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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.retry.annotation.Recover;

import static org.junit.jupiter.api.Assertions.*;

class RecoverMethodFilterTest {
    private static class Pojo {
        @Recover
        public Object annotated(final Exception e) {
            return null;
        }

        public Object notAnnotated(final Exception e) {
            return null;
        }
    }


    private RecoverMethodFilter recoverMethodFilter;

    @BeforeEach
    void setUp() {
        recoverMethodFilter = new RecoverMethodFilter();
    }

    @Test
    @Recover
    void filterAnnotated() throws NoSuchMethodException {
        assertTrue(recoverMethodFilter.filter(new Pojo(), Pojo.class.getDeclaredMethod("annotated", Exception.class)));
    }

    @Test
    void filterNotAnnotated() throws NoSuchMethodException {
        assertFalse(recoverMethodFilter.filter(new Pojo(), Pojo.class.getDeclaredMethod("notAnnotated", Exception.class)));
    }
}