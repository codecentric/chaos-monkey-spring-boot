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
package de.codecentric.spring.boot.chaos.monkey.assaults;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.management.OperatingSystemMXBean;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** @author Lukas Morawietz */
abstract class CPUAssaultIntegration {

    @SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
            "management.endpoints.web.exposure.include=chaosmonkey", "management.endpoints.enabled-by-default=true",
            "chaos.monkey.assaults.cpuActive=true", "chaos.monkey.assaults.cpuLoadTargetFraction=0.3",
            "chaos.monkey.assaults.cpuMillisecondsHoldLoad=5000", "spring.profiles.active=chaos-monkey"})
    static class LowCPUAssaultIntegration extends CPUAssaultIntegration {
    }

    @SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
            "management.endpoints.web.exposure.include=chaosmonkey", "management.endpoints.enabled-by-default=true",
            "chaos.monkey.assaults.cpuActive=true", "chaos.monkey.assaults.cpuLoadTargetFraction=0.8",
            "chaos.monkey.assaults.cpuMillisecondsHoldLoad=5000", "spring.profiles.active=chaos-monkey"})
    static class HighCPUAssaultIntegration extends CPUAssaultIntegration {
    }

    @SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
            "management.endpoints.web.exposure.include=chaosmonkey", "management.endpoints.enabled-by-default=true",
            "chaos.monkey.assaults.cpuActive=true", "chaos.monkey.assaults.cpuLoadTargetFraction=1.0",
            "chaos.monkey.assaults.cpuMillisecondsHoldLoad=5000", "spring.profiles.active=chaos-monkey"})
    static class MaxCPUAssaultIntegration extends CPUAssaultIntegration {
    }

    @Autowired
    private CpuAssault cpuAssault;

    @Autowired
    private ChaosMonkeySettings settings;

    @NotNull
    private boolean isCpuAssaultActiveOriginal;

    @NotNull
    private double cpuLoadTargetFraction;

    @BeforeEach
    void setUp() {
        isCpuAssaultActiveOriginal = settings.getAssaultProperties().isCpuActive();
        cpuLoadTargetFraction = settings.getAssaultProperties().getCpuLoadTargetFraction();
    }

    @AfterEach
    void tearDown() {
        settings.getAssaultProperties().setCpuActive(isCpuAssaultActiveOriginal);
    }

    @Test
    void cpuAssault_configured() {
        assertNotNull(cpuAssault);
        assertTrue(cpuAssault.isActive());
    }

    @Test
    void runAttack() {
        OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        long start = System.nanoTime();

        Thread backgroundThread = new Thread(cpuAssault::attack);
        backgroundThread.start();

        // make sure we timeout if we never reach the target fill fraction
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30)) {
            // if we reach target approximately (cpu filled up
            // correctly) check if it is still held after some time (and not just passed)
            if (isInRange(os.getProcessCpuLoad(), cpuLoadTargetFraction, 0.05)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                assertTrue(isInRange(os.getProcessCpuLoad(), cpuLoadTargetFraction, 0.05),
                        String.format("Failed to hold CPU load. Was %.2f %% but should have been %.2f %%", os.getProcessCpuLoad() * 100,
                                cpuLoadTargetFraction * 100));
                return;
            }
            // have to wait between checks to make sure the test thread doesn't generate too
            // much load
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        // if timeout reached
        fail(String.format("CPU did not fill up in time. Filled %.2f %% but should have filled %.2f %%", os.getProcessCpuLoad() * 100,
                cpuLoadTargetFraction * 100));
    }

    /**
     * Checks if `value` is in range of designated `target`, depending on given
     * `deviationFactor`
     *
     * @param value
     *            value to check against target if its in range
     * @param deviationFactor
     *            factor in percentage (10% = 0.1) of how much value is allowed to
     *            deviate from target
     * @param target
     *            value against value is checked against
     * @return true if in range
     */
    private boolean isInRange(double value, double target, double deviationFactor) {
        double deviation = target * deviationFactor;
        double lowerBoundary = Math.max(target - deviation, 0);
        double upperBoundary = Math.max(target + deviation, 1);

        return value >= lowerBoundary && value <= upperBoundary;
    }
}
