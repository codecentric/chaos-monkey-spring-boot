package de.codecentric.spring.boot.chaos.monkey.events;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Benjamin Wilms
 */
public class MetricEvent extends ApplicationEvent {
    private MetricType metricType;
    private AtomicInteger gaugeValue;
    private String methodSignature;
    private String[] tags;

    /**
     * Create a new Chaos Monkey for Spring Boot Metric ApplicationEvent.
     *
     * @param source          the object on which the event initially occurred (never {@code null})
     * @param metricType      MetricType
     * @param methodSignature String
     * @param tags            String[]
     */
    public MetricEvent(Object source, MetricType metricType, String methodSignature, String... tags) {
        super(source);
        this.metricType = metricType;
        this.methodSignature = methodSignature;
        this.tags = tags;
    }

    public MetricEvent(Object source, MetricType metricType, String... tags) {
        super(source);
        this.metricType = metricType;
        this.tags = tags;
    }

    public MetricEvent(Object source, MetricType metricType, AtomicInteger gaugeValue) {
        super(source);
        this.metricType = metricType;
        this.gaugeValue = gaugeValue;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String[] getTags() {
        return tags;
    }

    public AtomicInteger getGaugeValue() {
        return gaugeValue;
    }
}
