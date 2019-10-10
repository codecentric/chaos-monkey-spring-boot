package de.codecentric.spring.boot.chaos.monkey.events;

import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Benjamin Wilms
 */
public class MetricEvent extends ApplicationEvent {
    private final MetricType metricType;
    private final double metricValue;
    private final String methodSignature;
    private final String[] tags;

    /**
     * Create a new Chaos Monkey for Spring Boot Metric ApplicationEvent.
     *
     * @param source          the object on which the event initially occurred (never {@code null})
     * @param metricType      MetricType
     * @param methodSignature String
     * @param tags            String[]
     */
    public MetricEvent(Object source, MetricType metricType, String methodSignature, String... tags) {
        this(source, metricType, -1, methodSignature, tags);
    }

    public MetricEvent(Object source, MetricType metricType, String... tags) {
        this(source, metricType, -1, null);
    }

    public MetricEvent(Object source, MetricType metricType, long metricValue, String methodSignature, String... tags) {
        super(source);
        this.metricType = metricType;
        this.tags = tags;
        this.methodSignature = methodSignature;
        this.metricValue = metricValue;
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

    public double getMetricValue() {
        return metricValue;
    }

    public AtomicInteger getGaugeValue() {
        return new AtomicInteger((int) metricValue);
    }
}
