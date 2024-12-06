/*
 * Copyright 2018-2024 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.util.AssertionErrors.assertFalse;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.AssaultPropertiesUpdate;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import java.util.concurrent.TimeUnit;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;

/** @author Benjamin Wilms */
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.endpoints.web.exposure.include=chaosmonkey", "management.endpoints.enabled-by-default=true",
        "chaos.monkey.assaults.memoryActive=true", "chaos.monkey.assaults.memoryFillTargetFraction=0.80",
        "chaos.monkey.assaults.memoryMillisecondsWaitNextIncrease=100", "chaos.monkey.assaults.memoryFillIncrementFraction=0.99",
        "chaos.monkey.assaults.memoryMillisecondsHoldFilledMemory=2000", "spring.profiles.active=chaos-monkey"})
class MemoryAssaultIntegrationTest {

    @LocalServerPort
    private int serverPort;

    private String baseUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemoryAssault memoryAssault;

    @Autowired
    private ChaosMonkeySettings settings;

    @NotNull
    private boolean isMemoryAssaultActiveOriginal;

    @NotNull
    private double memoryFillTargetFraction;

    @BeforeEach
    void setUp() {
        isMemoryAssaultActiveOriginal = settings.getAssaultProperties().isMemoryActive();
        memoryFillTargetFraction = settings.getAssaultProperties().getMemoryFillTargetFraction();
        baseUrl = "http://localhost:" + this.serverPort + "/actuator/chaosmonkey";
    }

    @AfterEach
    void tearDown() {
        settings.getAssaultProperties().setMemoryActive(isMemoryAssaultActiveOriginal);
    }

    @Test
    void memoryAssault_configured() {
        assertNotNull(memoryAssault);
        assertTrue(memoryAssault.isActive());
    }

    @Test
    void runAttack() {
        Runtime rt = Runtime.getRuntime();
        long start = System.nanoTime();

        Thread backgroundThread = new Thread(memoryAssault::attack);
        backgroundThread.start();

        // make sure we time out if we never reach the target fill fraction
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30)) {
            // if we reach target approximately (memory filled up
            // correctly) we can return (test is successful)
            double target = rt.maxMemory() * memoryFillTargetFraction;
            if (isInRange(rt.totalMemory(), target)) {
                return;
            }
        }

        // if timeout reached
        long bytes = (long) (rt.maxMemory() * memoryFillTargetFraction);
        fail("Memory did not fill up in time. Filled " + DataSize.ofBytes(rt.totalMemory()).toMegabytes() + " MB but should have filled "
                + DataSize.ofBytes(bytes).toMegabytes() + " MB");
    }

    /**
     * Checks if `value` is in range of designated `target`, depending on given
     * `deviationFactor`
     *
     * @param value
     *            value to check against target if its in range
     * @param target
     *            value against value is checked against
     * @return true if in range
     */
    private boolean isInRange(double value, double target) {
        // factor in percentage (10% = 0.1) of how much value is allowed to deviate from
        // target
        double deviationFactor = 0.2;
        double deviation = target * deviationFactor;
        double lowerBoundary = Math.max(target - deviation, 0);
        double upperBoundary = Math.max(target + deviation, Runtime.getRuntime().maxMemory());

        return value >= lowerBoundary && value <= upperBoundary;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    void runAndAbortAttack() throws Throwable {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setMemoryActive(false);

        Runtime rt = Runtime.getRuntime();
        long start = System.nanoTime();

        long usedMemoryBeforeAttack = rt.totalMemory() - rt.freeMemory();
        Thread backgroundThread = new Thread(memoryAssault::attack);

        backgroundThread.start();
        Thread.sleep(100);
        long usedMemoryDuringAttack = rt.totalMemory() - rt.freeMemory();

        assertTrue(usedMemoryBeforeAttack <= usedMemoryDuringAttack);

        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);
        assertEquals(200, result.getStatusCode().value());

        while (backgroundThread.isAlive() && System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30)) {
            // wait for thread to finish gracefully or time out
        }

        assertFalse("Assault is still running", backgroundThread.isAlive());

        // Apparently java needs a bit more time to finish up things
        Thread.sleep(1000);

        long usedMemoryAfterAttack = rt.totalMemory() - rt.freeMemory();

        // garbage collection should have run by now
        assertTrue(usedMemoryAfterAttack <= usedMemoryDuringAttack,
                "Memory after attack was " + DataSize.ofBytes(usedMemoryAfterAttack).toMegabytes()
                        + " MB but should have been less  amount of memory during attack (" + DataSize.ofBytes(usedMemoryDuringAttack).toMegabytes()
                        + " MB).");
    }

    @Test
    void allowInterruptionOfAssaultDuringHoldPeriod() throws Throwable {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setMemoryActive(false);

        Runtime rt = Runtime.getRuntime();
        long start = System.nanoTime();

        Thread backgroundThread = new Thread(memoryAssault::attack);
        assertFalse("Assault already active", backgroundThread.isAlive());

        backgroundThread.start();
        assertTrue(backgroundThread.isAlive(), "Assault not active");

        outer : {
            double fillTargetMemory = rt.maxMemory() * memoryFillTargetFraction;
            while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30)) {
                long totalMemoryDuringAttack = rt.totalMemory();
                if (isInRange(totalMemoryDuringAttack, fillTargetMemory)) {
                    break outer;
                }
            }

            long bytes = rt.totalMemory();
            fail("Memory did not fill up in time. Filled " + DataSize.ofBytes(bytes).toMegabytes() + " MB but should have filled "
                    + DataSize.ofBytes((long) fillTargetMemory).toMegabytes() + " MB");
        }

        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);
        assertEquals(200, result.getStatusCode().value(), "Request was not successful");

        // Apparently java needs a bit more time to finish up things
        Thread.sleep(1000);

        assertFalse("Assault is still running", backgroundThread.isAlive());
    }
}
