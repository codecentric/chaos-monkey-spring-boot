/*
 * Copyright 2021-2022 the original author or authors.
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

import com.sun.management.OperatingSystemMXBean;
import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuAssault implements ChaosMonkeyRuntimeAssault {
    private static final double leeway = 0.05;
    private static final Logger Logger = LoggerFactory.getLogger(CpuAssault.class);

    private final ChaosMonkeySettings settings;

    private final MetricEventPublisher metricEventPublisher;
    private final OperatingSystemMXBean os;

    public CpuAssault(OperatingSystemMXBean os, ChaosMonkeySettings settings, MetricEventPublisher metricEventPublisher) {
        this.os = os;
        this.settings = settings;
        this.metricEventPublisher = metricEventPublisher;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isCpuActive();
    }

    @Override
    public void attack() {
        Logger.info("Chaos Monkey - cpu assault");

        // metrics
        if (metricEventPublisher != null) {
            metricEventPublisher.publishMetricEvent(MetricType.CPU_ASSAULT);
        }
        double load = settings.getAssaultProperties().getCpuLoadTargetFraction();

        if (os.getProcessCpuLoad() >= 0) {
            ThreadManager threadManager = new ThreadManager(os, load);
            // initial ramp up
            while (os.getProcessCpuLoad() < load && isActive()) {
                threadManager.tick();
            }
            final long targetMs = System.currentTimeMillis() + settings.getAssaultProperties().getCpuMillisecondsHoldLoad();
            // hold for specified time
            while (targetMs > System.currentTimeMillis() && isActive()) {
                threadManager.tick();
            }
            threadManager.stop();
            Logger.info("Chaos Monkey - cpu assault cleaned up");
        } else {
            Logger.warn("Chaos Monkey - cpu information not available, assault not executed");
        }
    }

    @Override
    public String getCronExpression(AssaultProperties assaultProperties) {
        return assaultProperties.getCpuCronExpression() != null
                ? assaultProperties.getCpuCronExpression()
                : assaultProperties.getRuntimeAssaultCronExpression();
    }

    private static class ThreadManager {
        private final OperatingSystemMXBean os;
        private final double targetLoad;
        private final List<WorkerThread> runningThreads = new ArrayList<>();
        private final List<WorkerThread> pausedThreads = new ArrayList<>();
        private double lastLoad;

        private ThreadManager(OperatingSystemMXBean os, double targetLoad) {
            this.os = os;
            this.targetLoad = targetLoad;
        }

        public void tick() {
            double load = os.getProcessCpuLoad();
            // only change things if we have new data
            if (load != lastLoad) {
                lastLoad = load;
                if (load < targetLoad) {
                    WorkerThread thread;
                    if (pausedThreads.isEmpty()) {
                        thread = new WorkerThread("CPU Assault thread " + runningThreads.size());
                        thread.start();
                    } else {
                        thread = pausedThreads.remove(0);
                        synchronized (thread) {
                            thread.shouldPause = false;
                            thread.notify();
                        }
                    }
                    runningThreads.add(thread);
                } else if (load > targetLoad + leeway && !runningThreads.isEmpty()) {
                    WorkerThread thread = runningThreads.remove(0);
                    thread.shouldPause = true;
                    pausedThreads.add(thread);
                }
                // make sure the managing thread doesn't generate too much load
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public void stop() {
            runningThreads.addAll(pausedThreads);
            for (Thread thread : runningThreads) {
                thread.interrupt();
                while (thread.isAlive()) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        thread.interrupt();
                    }
                }
            }
        }
    }

    private static class WorkerThread extends Thread {
        private volatile boolean shouldPause = false;

        public WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            long f1 = 0;
            long f2 = 1;
            while (!interrupted()) {
                try {
                    if (shouldPause) {
                        synchronized (this) {
                            wait();
                        }
                    }
                    // next fibonacci number
                    f2 = f1 + f2;
                    f1 = f2 - f1;
                } catch (InterruptedException e) {
                    // set interrupt flag for outer check
                    interrupt();
                }
            }
        }
    }
}
