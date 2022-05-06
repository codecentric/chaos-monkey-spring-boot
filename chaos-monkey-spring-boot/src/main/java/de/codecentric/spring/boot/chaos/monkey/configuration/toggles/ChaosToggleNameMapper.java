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

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;

/**
 * A way to map individual ChaosTargets (controllers, repositories, etc) and the
 * corresponding method. Implementations can make the name to toggle mapping as
 * coarse or as detailed as desired.
 *
 * @author Clint Checketts
 */
public interface ChaosToggleNameMapper {

    /**
     * @param type
     *            ChaosType (controller, repository, etc)
     * @param name
     *            Name of item being assaulted (a method class and method name for
     *            example)
     * @return the toggle name to be switched
     */
    String mapName(ChaosTarget type, String name);
}
