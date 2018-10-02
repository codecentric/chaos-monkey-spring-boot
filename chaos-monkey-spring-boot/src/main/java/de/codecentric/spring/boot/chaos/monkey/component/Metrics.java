package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.events.MetricEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.atomic.AtomicInteger;

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


    private void gauge(MetricType type, AtomicInteger number) {
        if (meterRegistry != null)
            meterRegistry.gauge(type.getMetricName() + ".gauge.", number);
    }

    private String extractComponent(String name) {
        return name.replaceAll("execution.", "");
    }

    @Override
    public void onApplicationEvent(MetricEvent event) {
        switch (event.getMetricType()) {
            case SERVICE:
                counterWatcher(event.getMetricType(), event.getMethodSignature());
                break;
            case COMPONENT:
                counterWatcher(event.getMetricType(), event.getMethodSignature());
                break;
            case REPOSITORY:
                counterWatcher(event.getMetricType(), event.getMethodSignature());
                break;
            case RESTCONTROLLER:
                counterWatcher(event.getMetricType(), event.getMethodSignature());
                break;
            case CONTROLLER:
                counterWatcher(event.getMetricType(), event.getMethodSignature());
                break;
            case LATENCY_ASSAULT:
                if (event.getGaugeValue() != null)
                    gauge(event.getMetricType(), event.getGaugeValue());

                counter(event.getMetricType(), event.getTags());
                break;
            case APPLICATION_REQ_COUNT:
                counter(event.getMetricType(), event.getTags());
                break;
            case KILLAPP_ASSAULT:
                counter(event.getMetricType(), event.getTags());
                break;
            case EXCEPTION_ASSAULT:
                counter(event.getMetricType(), event.getTags());
                break;
            default:

        }
    }
}
