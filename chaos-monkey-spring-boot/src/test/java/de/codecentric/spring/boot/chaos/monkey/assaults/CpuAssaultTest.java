/*
 * Copyright 2021-2026 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

class CpuAssaultTest {

    @Test
    void repeatedLoadSamplesArePolledAtIntervals() {
        OperatingSystemMXBean os = mock(OperatingSystemMXBean.class);
        when(os.getProcessCpuLoad()).thenReturn(0.25);
        CpuAssault.ThreadManager threadManager = new CpuAssault.ThreadManager(os, 0.5);

        try {
            threadManager.tick();
            long start = System.nanoTime();
            threadManager.tick();

            assertThat(System.nanoTime() - start).isGreaterThanOrEqualTo(150_000_000L);
        } finally {
            threadManager.stop();
        }
    }
}
