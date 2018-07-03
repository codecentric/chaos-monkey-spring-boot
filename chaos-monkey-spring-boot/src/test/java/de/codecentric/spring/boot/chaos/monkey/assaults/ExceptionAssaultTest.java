/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.assaults;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Thorsten Deelmann
 */
public class ExceptionAssaultTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void throwsRuntimeException() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Chaos Monkey - RuntimeException");

        ExceptionAssault exceptionAssault = new ExceptionAssault(true);
        exceptionAssault.attack();
    }
}