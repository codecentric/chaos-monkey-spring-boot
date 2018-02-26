package de.mrbwilms.spring.boot.chaos.monkey.component;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Benjamin Wilms
 */
@Component
public class ChaosMonkey {

    @Autowired
    private ApplicationContext context;

    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);

    private final int level;
    private final int timeoutRangeStart;
    private final int timeoutRangeEnd;
    private boolean addLatency;
    private boolean createException;
    private boolean killApplication;

    public ChaosMonkey(Environment env) {
        this.env = env;

        addLatency = this.env.getProperty("chaos.monkey.attackmode.latency", Boolean.class, true);
        createException = this.env.getProperty("chaos.monkey.attackmode.exception", Boolean.class, false);
        killApplication = this.env.getProperty("chaos.monkey.attackmode.kill", Boolean.class, false);
        level = this.env.getProperty("chaos.monkey.level", Integer.class, 5);
        timeoutRangeStart = this.env.getProperty("chaos.monkey.timeout.range.start", Integer.class, 3000);
        timeoutRangeEnd = this.env.getProperty("chaos.monkey.timeout.range.end", Integer.class, 10000);

    }


    public void callChaosMonkey() {
        // Trouble?
        int troubleRand = RandomUtils.nextInt(0, 10);
        int exceptionRand = RandomUtils.nextInt(0, 10);

        if (troubleRand > level) {

            if (addLatency && createException) {
                // Timeout or Exception?
                if (exceptionRand < 7) {
                    generateLatency();
                } else {
                    generateChaosException();
                }
            } else if (addLatency) {
                generateLatency();
            } else if (createException) {
                generateChaosException();
            } else if (killApplication) {
                killTheBossApp();
            }

        }
    }

    private void killTheBossApp() {

        try {
            LOGGER.info("Chaos Monkey - I am killing your Application!");

            int exit = SpringApplication.exit(context, new ExitCodeGenerator() {
                public int getExitCode() {
                    return 0;
                }
            });
            System.exit(exit);
        } catch (Exception e) {
            LOGGER.info("Chaos Monkey - Unable to kill the App, I am not the BOSS!");
        }
    }

    private void generateChaosException() {
        LOGGER.info("Chaos Monkey - exception");
        throw new RuntimeException("Chaos Monkey - RuntimeException");
    }

    /***
     * Generates a timeout exception
     */
    private void generateLatency() {
        LOGGER.info("Chaos Monkey - timeout");
        int timeout = RandomUtils.nextInt(timeoutRangeStart, timeoutRangeEnd);

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }


}
