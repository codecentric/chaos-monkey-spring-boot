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

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Thorsten Deelmann
 */
public class LatencyAssault implements ChaosMonkeyAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyAssault.class);

    private int latencyRangeStart;
    private int latencyRangeEnd;
    private boolean active;

    public LatencyAssault(@Min(value = 1) @Max(value = Integer.MAX_VALUE) int latencyRangeStart,
                          @Min(value = 1) @Max(value = Integer.MAX_VALUE) int latencyRangeEnd,
                          boolean latencyActive) {
        this.latencyRangeStart = latencyRangeStart;
        this.latencyRangeEnd = latencyRangeEnd;
        active = latencyActive;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void attack() {
        LOGGER.info("Chaos Monkey - timeout");
        int timeout = RandomUtils.nextInt(latencyRangeStart, latencyRangeEnd);

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
