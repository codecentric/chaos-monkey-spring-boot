package de.mrbw.chaos.monkey.component;

import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Benjamin Wilms
 */
@Component
public class ChaosMonkey {

    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);

    private final boolean active;
    private final int level;
    private final int timeoutRangeStart;
    private final int timeoutRangeEnd;

    public ChaosMonkey(Environment env) {
        this.env = env;

        active = this.env.getProperty("chaos.monkey.active", Boolean.class, false);
        level = this.env.getProperty("chaos.monkey.level", Integer.class, 5);
        timeoutRangeStart = this.env.getProperty("chaos.monkey.timeout.range.start", Integer.class, 3000);
        timeoutRangeEnd = this.env.getProperty("chaos.monkey.timeout.range.end", Integer.class, 10000);

    }



    public void callChaosMonkey() {
        if (active) {
            // Trouble?
            int troubleRand = RandomUtils.nextInt(0, 10);
            int exceptionRand = RandomUtils.nextInt(0, 10);

            if (troubleRand > level) {
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
        int timeout = RandomUtils.nextInt(timeoutRangeStart, timeoutRangeEnd);

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }


}
