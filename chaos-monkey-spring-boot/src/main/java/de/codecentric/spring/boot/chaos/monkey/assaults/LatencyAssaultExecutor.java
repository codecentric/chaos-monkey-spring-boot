package de.codecentric.spring.boot.chaos.monkey.assaults;

public class LatencyAssaultExecutor implements ChaosMonkeyLatencyAssaultExecutor {
    @Override
    public void execute(long durationInMillis) {
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
