package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "chaos.monkey.assaults.memoryActive=true",
                "chaos.monkey.assaults.memoryFillTargetFraction=0.5",
                "chaos.monkey.assaults.memoryMillisecondsWaitNextIncrease=100",
                "chaos.monkey.assaults.memoryMillisecondsHoldFilledMemory=1500",
                "spring.profiles.active=chaos-monkey"})
public class MemoryAssaultIntegrationTest {

    @Autowired
    private MemoryAssault memoryAssault;

    @Autowired
    private ChaosMonkeySettings settings;

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
                if (remaining <= initialMemory / 2) return;
            }

            fail("Memory did not reach half exhaustion between timeout");
        } finally {
            backgroundThread.join();
        }
    }

    @Test
    public void runAndAbortAttack() throws Throwable {
        Runtime rt = Runtime.getRuntime();
        long initialMemory = rt.freeMemory();
        long start = System.nanoTime();

        boolean[] stillActive = new boolean[]{true};
        AssaultProperties originalAssaultProperties = settings.getAssaultProperties();
        AssaultProperties mockAssaultConfig = mock(AssaultProperties.class);
        when(mockAssaultConfig.getMemoryFillTargetFraction()).thenReturn(0.75);
        when(mockAssaultConfig.getMemoryMillisecondsHoldFilledMemory()).thenReturn(10000);
        when(mockAssaultConfig.getMemoryMillisecondsWaitNextIncrease()).thenReturn(100);
        when(mockAssaultConfig.isMemoryActive()).thenAnswer(iom -> stillActive[0]);

        try {
            settings.setAssaultProperties(mockAssaultConfig);

            Thread backgroundThread = new Thread(memoryAssault::attack);
            backgroundThread.start();

            Thread.sleep(100);
            stillActive[0] = false;
            while (backgroundThread.isAlive() && System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
                long remaining = rt.freeMemory();
                if (remaining <= initialMemory / 2) fail("Exhausted 50% of free memory even after cancellation");
            }
        } finally {
            settings.setAssaultProperties(originalAssaultProperties);
        }
    }

    @Test
    public void allowInterruptionOfAssaultDuringHoldPeriod() throws Throwable {
        Runtime rt = Runtime.getRuntime();
        long initialMemory = rt.freeMemory();
        long start = System.nanoTime();

        boolean[] stillActive = new boolean[]{true};
        AssaultProperties originalAssaultProperties = settings.getAssaultProperties();
        AssaultProperties mockAssaultConfig = mock(AssaultProperties.class);
        when(mockAssaultConfig.getMemoryFillTargetFraction()).thenReturn(0.5);
        when(mockAssaultConfig.getMemoryMillisecondsHoldFilledMemory()).thenReturn(10000);
        when(mockAssaultConfig.getMemoryMillisecondsWaitNextIncrease()).thenReturn(100);
        when(mockAssaultConfig.isMemoryActive()).thenAnswer(iom -> stillActive[0]);


        try {
            Thread backgroundThread = new Thread(memoryAssault::attack);
            backgroundThread.start();

            outer: {
                while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
                    long remaining = rt.freeMemory();
                    if (remaining <= initialMemory / 2) break outer;
                }

                fail("Memory did not reach half exhaustion before timeout");
            }

            stillActive[0] = false;

            backgroundThread.join(5000);
            assertFalse(backgroundThread.isAlive());
        } finally {
            settings.setAssaultProperties(originalAssaultProperties);
        }
    }

}