package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.endpoints.AssaultPropertiesUpdate;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.endpoints.web.exposure.include=chaosmonkey",
        "management.endpoints.enabled-by-default=true",
        "chaos.monkey.assaults.memoryActive=true", "chaos.monkey.assaults.memoryFillTargetFraction=0.90",
        "chaos.monkey.assaults.memoryMillisecondsWaitNextIncrease=100",
        "chaos.monkey.assaults.memoryFillIncrementFraction=1",
        "chaos.monkey.assaults.memoryMillisecondsHoldFilledMemory=15000", "spring.profiles.active=chaos-monkey"})
public class MemoryAssaultIntegrationTest {
    @LocalServerPort
    private int serverPort;

    private String baseUrl;

    @Autowired
    private TestRestTemplate restTemplate;


    @Autowired
    private MemoryAssault memoryAssault;

    @Autowired
    private ChaosMonkeySettings settings;

    @NotNull boolean isMemoryAssaultActiveOrignal;

    @Before
    public void setUp() throws Exception {
        isMemoryAssaultActiveOrignal = settings.getAssaultProperties().isMemoryActive();
        baseUrl = "http://localhost:" + this.serverPort + "/actuator/chaosmonkey";
    }

    @After
    public void tearDown(){
        settings.getAssaultProperties().setMemoryActive(isMemoryAssaultActiveOrignal);
    }

    @Test
    public void memoryAssault_configured() {
        assertNotNull(memoryAssault);
        assertTrue(memoryAssault.isActive());
    }

    @Test
    public void memoryAssault_runAttack() throws Exception {
        Runtime rt = Runtime.getRuntime();
        long initialMemory = rt.freeMemory();
        long start = System.nanoTime();

        Thread backgroundThread = new Thread(memoryAssault::attack);
        backgroundThread.start();

        try {
            while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
                long remaining = rt.freeMemory();
                if (remaining <= initialMemory / 2)
                    return;
            }

            fail("Memory did not reach half exhaustion between timeout");
        } finally {
            backgroundThread.join();
        }
    }

    @Test
    public void runAndAbortAttack() throws Throwable {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setMemoryActive(false);

        Runtime rt = Runtime.getRuntime();
        long initialMemory = rt.freeMemory();
        long start = System.nanoTime();


        Thread backgroundThread = new Thread(memoryAssault::attack);
        backgroundThread.start();

        Thread.sleep(100);
        ResponseEntity<String> result =
                restTemplate.postForEntity(baseUrl + "/assaults", assaultProperties,
                        String.class);
        assertEquals(200, result.getStatusCodeValue());

        while (backgroundThread.isAlive() && System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
            long remaining = rt.freeMemory();
            if (remaining <= initialMemory / 4)
                fail("Exhausted 75% of free memory even after cancellation");
        }
    }

    @Test
    public void allowInterruptionOfAssaultDuringHoldPeriod() throws Throwable {
        Runtime rt = Runtime.getRuntime();
        long initialMemory = rt.freeMemory();
        long start = System.nanoTime();

        AtomicBoolean stillActive = new AtomicBoolean(true);
        AssaultProperties originalAssaultProperties = settings.getAssaultProperties();
        AssaultProperties mockAssaultConfig = mock(AssaultProperties.class);
        when(mockAssaultConfig.getMemoryFillTargetFraction()).thenReturn(0.25);
        when(mockAssaultConfig.getMemoryMillisecondsHoldFilledMemory()).thenReturn(10000);
        when(mockAssaultConfig.getMemoryMillisecondsWaitNextIncrease()).thenReturn(100);
        when(mockAssaultConfig.isMemoryActive()).thenAnswer(iom -> stillActive.get());

        try {
            Thread backgroundThread = new Thread(memoryAssault::attack);
            backgroundThread.start();

            outer:
            {
                while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
                    long remaining = rt.freeMemory();
                    if (remaining * 4.0 / 3.0 <= initialMemory)
                        break outer;
                }

                fail("Memory did not reach half exhaustion before timeout");
            }

            stillActive.set(false);

            backgroundThread.join(7500);
            assertFalse(backgroundThread.isAlive());
        } finally {
            settings.setAssaultProperties(originalAssaultProperties);
        }
    }

}
