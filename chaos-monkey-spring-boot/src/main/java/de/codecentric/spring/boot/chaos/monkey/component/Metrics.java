package de.codecentric.spring.boot.chaos.monkey.component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * @author Benjamin Wilms
 */
public class Metrics {


    private final MeterRegistry meterRegistry;

    public Metrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }



    public Counter counter(MetricType type, String... tags) {

        return meterRegistry.counter(type.getMetricName(), tags);
    }

    public Counter counter(String name) {

        return meterRegistry.counter(name);
    }

    public Counter counterWatcher(MetricType type, String name) {
        String component = name.substring(name.indexOf("."), name.indexOf(".", name.indexOf(".")));

        return meterRegistry.counter(type.getMetricName() + "watcher." + component, "component", name);
    }

    public Timer timer(MetricType type) {

        return meterRegistry.timer(type.getMetricName());
    }

    public <T extends Number> T gauge(MetricType type, T number) {

        return meterRegistry.gauge(type.getMetricName() + "gauge", number);
    }
}
