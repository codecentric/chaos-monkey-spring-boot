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
package de.codecentric.spring.boot.chaos.monkey.assaults;

/**
 * A way to interfere with the application. Implementations will be either
 * {@link ChaosMonkeyRuntimeAssault} or {@link ChaosMonkeyRequestAssault},
 * depending if the interference is on the request or runtime level.
 *
 * <p>
 * Implementing this interface directly is discouraged, and will generally be
 * treated as a Request-level assault
 *
 * @author Thorsten Deelmann
 */
public interface ChaosMonkeyAssault {

    boolean isActive();

    void attack();
}
