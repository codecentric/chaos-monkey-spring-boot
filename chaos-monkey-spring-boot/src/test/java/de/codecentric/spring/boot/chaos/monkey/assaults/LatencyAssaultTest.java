package de.codecentric.spring.boot.chaos.monkey.assaults;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LatencyAssault.class, RandomUtils.class})
public class LatencyAssaultTest {

    @Test
    public void threadSleepHasBeenCalled() throws Exception {
        mockStatic(Thread.class);
        mockStatic(RandomUtils.class);
        int sleepTimeMillis = 150;
        int latencyRangeStart = 100;
        int latencyRangeStop = 200;
        when(RandomUtils.nextInt(latencyRangeStart, latencyRangeStop)).thenReturn(sleepTimeMillis);

        LatencyAssault latencyAssault = new LatencyAssault(latencyRangeStart, latencyRangeStop, true);
        latencyAssault.attack();

        verifyStatic(Thread.class, times(1));
        Thread.sleep(sleepTimeMillis);
    }
}