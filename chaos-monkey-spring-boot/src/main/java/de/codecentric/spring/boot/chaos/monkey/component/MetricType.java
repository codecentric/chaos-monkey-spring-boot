package de.codecentric.spring.boot.chaos.monkey.component;

/**
 * @author Benjamin Wilms
 */
public enum MetricType {

    CONTROLLER("controller"), RESTCONTROLLER("restController"), REPOSITORY("repository"), COMPONENT("component"), SERVICE("service"),
    LATENCY_ASSAULT("assault.latency"), APPLICATION_REQ_COUNT("application.requests");

    private String metricName;

    MetricType(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        String metricBaseName = "chaos.monkey.";
        return metricBaseName + metricName;
    }


}
