package de.codecentric.spring.boot.chaos.monkey.assaults;

public interface ChaosMonkeyLatencyAssaultExecutor {

    void execute(long duration);
}
