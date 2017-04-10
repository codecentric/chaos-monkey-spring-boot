package de.mrbw.chaos.monkey.component;

import org.apache.commons.lang3.RandomUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;

/**
 * @author Benjamin Wilms
 */
@Aspect
public class ChaosMonkey {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);

    private DynamicBooleanProperty chaosMonkey =
        DynamicPropertyFactory.getInstance().getBooleanProperty("chaos.monkey.active", false);

    private DynamicIntProperty chaosMonkeyLevel = DynamicPropertyFactory.getInstance().getIntProperty("chaos.monkey.level", 5);

    private DynamicIntProperty timeoutRangeStart =
        DynamicPropertyFactory.getInstance().getIntProperty("chaos.monkey.timeout.range.start", 3000);

    private DynamicIntProperty timeoutRangeEnd =
        DynamicPropertyFactory.getInstance().getIntProperty("chaos.monkey.timeout.range.end", 10000);

    public ChaosMonkey() {

        String chaosMonkeyStatus = chaosMonkey.get() ? "bad mood or evil" : "Eats bananas or sleeps";

        LOGGER.info(chaosMonkeyStatus);
    }

    @Around("execution(* de.codecentric.resilient..*.*Service.*(..))")
    public Object createConnoteHystrix(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.debug(LOGGER.isDebugEnabled() ? "After Connote Service Call: createConnoteChaos()" : null);

        chaosMonkey();

        return pjp.proceed();
    }

    private void chaosMonkey() {
        if (chaosMonkey.get()) {
            // Trouble?
            int troubleRand = RandomUtils.nextInt(0, 10);
            int exceptionRand = RandomUtils.nextInt(0, 10);

            if (troubleRand > chaosMonkeyLevel.get()) {
                LOGGER.debug("Chaos Monkey - generates trouble");
                // Timeout or Exception?
                if (exceptionRand < 7) {
                    LOGGER.debug("Chaos Monkey - timeout");
                    // Timeout
                    generateTimeout();
                } else {
                    LOGGER.debug("Chaos Monkey - exception");
                    // Exception
                    throw new RuntimeException("Chaos Monkey - RuntimeException");
                }
            }
        }
    }

    /***
     * Generates a timeout exception
     */
    private void generateTimeout() {
        int timeout = RandomUtils.nextInt(timeoutRangeStart.get(), timeoutRangeEnd.get());

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing, hystrix tries to interrupt
        }
    }
}
