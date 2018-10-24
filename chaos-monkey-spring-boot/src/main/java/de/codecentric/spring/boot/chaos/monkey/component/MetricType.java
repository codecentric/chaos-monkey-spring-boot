package de.codecentric.spring.boot.chaos.monkey.component;

/**
 * @author Benjamin Wilms
 */
public enum MetricType {

    CONTROLLER("controller"), RESTCONTROLLER("restController"), REPOSITORY("repository"), COMPONENT("component"), SERVICE("service"),
    LATENCY_ASSAULT("assault.latency.count"), EXCEPTION_ASSAULT("assault.exception.count"), KILLAPP_ASSAULT("assault.killapp.count"),
    APPLICATION_REQ_COUNT
            ("application.request.count"), MEMORY_ASSAULT("assault.memory.count");

    private String metricName;

    MetricType(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        String metricBaseName = "chaos.monkey.";
        return metricBaseName + metricName;
    }


}
