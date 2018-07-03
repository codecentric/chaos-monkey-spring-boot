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

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thorsten Deelmann
 */
public class ExceptionAssault implements ChaosMonkeyAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAssault.class);
    private ChaosMonkeySettings settings;

    public ExceptionAssault(ChaosMonkeySettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isExceptionsActive();
    }

    @Override
    public void attack() {
        LOGGER.info("Chaos Monkey - exception");
        throw new RuntimeException("Chaos Monkey - RuntimeException");
    }
}
