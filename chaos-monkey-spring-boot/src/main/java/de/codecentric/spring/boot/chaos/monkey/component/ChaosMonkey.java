package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Benjamin Wilms
 */
@Component
public class ChaosMonkey {

    private final ChaosMonkeyProperties chaosMonkeyProperties;
    private final AssaultProperties assaultProperties;
    @Autowired
    private ApplicationContext context;


    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);


    public ChaosMonkey(ChaosMonkeyProperties chaosMonkeyProperties, AssaultProperties assaultProperties) {
        this.assaultProperties = assaultProperties;
        this.chaosMonkeyProperties = chaosMonkeyProperties;

    }


    public void callChaosMonkey() {
        if (isTrouble() && isEnabled()) {
            int exceptionRand = assaultProperties.getExceptionRandom();

            if (assaultProperties.isLatencyActive() && assaultProperties.isExceptionsActive()) {
                // Timeout or Exception?
                if (exceptionRand < 7) {
                    generateLatency();
                } else {
                    generateChaosException();
                }
            } else if (assaultProperties.isLatencyActive()) {
                generateLatency();
            } else if (assaultProperties.isExceptionsActive()) {
                generateChaosException();
            } else if (assaultProperties.isKillApplicationActive()) {
                killTheBossApp();
            }

        }
    }

    private boolean isTrouble() {
        return assaultProperties.getTroubleRandom() >= assaultProperties.getLevel();
    }

    private boolean isEnabled() {
        return this.chaosMonkeyProperties.isEnabled();
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
        int timeout = RandomUtils.nextInt(assaultProperties.getLatencyRangeStart(), assaultProperties.getLatencyRangeEnd());

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }


}
