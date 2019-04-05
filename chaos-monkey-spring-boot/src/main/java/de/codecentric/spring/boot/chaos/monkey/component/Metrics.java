package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.events.MetricEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationListener;

/**
 * @author Benjamin Wilms
 */
public class Metrics implements ApplicationListener<MetricEvent> {


    private MeterRegistry meterRegistry;

    public Metrics() {
        this.meterRegistry = io.micrometer.core.instrument.Metrics.globalRegistry;

    }

    private void counter(MetricType type, String... tags) {
        if (meterRegistry != null)
            meterRegistry.counter(type.getMetricName(), tags).increment();
    }

    private void counterWatcher(MetricType type, String name) {
        if (meterRegistry != null)
            meterRegistry.counter(type.getMetricName() + ".watcher", "component", extractComponent(name)).increment();
    }


    private void gauge(MetricType type, double number) {
        if (meterRegistry != null)
            meterRegistry.gauge(type.getMetricName() + ".gauge.", number);
    }

    private String extractComponent(String name) {
        return name.replaceAll("execution.", "");
    }

    @Override
    public void onApplicationEvent(MetricEvent event) {
        if (event.getMetricType().isSignatureOnlyEvent()) {
            counterWatcher(event.getMetricType(), event.getMethodSignature());
        } else if (event.getMetricType().isTagEvent()) {
            counter(event.getMetricType(), event.getTags());
        } else { // untagged and without method signature -> metric value is the interesting part
            gauge(event.getMetricType(), event.getMetricValue());
            counter(event.getMetricType(), event.getTags());
        }
    }
}
