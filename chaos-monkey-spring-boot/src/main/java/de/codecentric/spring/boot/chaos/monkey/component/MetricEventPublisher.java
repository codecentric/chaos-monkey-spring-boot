package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.events.MetricEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Benjamin Wilms
 */
public class MetricEventPublisher {
    private ApplicationEventPublisher publisher;

    public MetricEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishMetricEvent(String signature, MetricType metricType, String... tags) {
        MetricEvent metricEvent = new MetricEvent(this, metricType, signature, tags);

        publisher.publishEvent(metricEvent);
    }

    public void publishMetricEvent(MetricType metricType, String... tags) {
        MetricEvent metricEvent = new MetricEvent(this, metricType, tags);

        publisher.publishEvent(metricEvent);
    }

    public void publishMetricEvent(MetricType metricType, AtomicInteger atomicTimeoutGauge) {
        MetricEvent metricEvent = new MetricEvent(this, metricType, atomicTimeoutGauge);
        publisher.publishEvent(metricEvent);
    }
}
