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
package com.example.chaos.monkey.toggledemo;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.toggles.DefaultChaosToggleNameMapper;

public class MyAppToggleMapper extends DefaultChaosToggleNameMapper {
    public MyAppToggleMapper(String prefix) {
        super(prefix);
    }

    @Override
    public String mapName(ChaosTarget type, String name) {
        if (type.equals(ChaosTarget.CONTROLLER) && name.toLowerCase().contains("hello")) {
            return this.togglePrefix + ".howdy";
        }
        return super.mapName(type, name);
    }
}