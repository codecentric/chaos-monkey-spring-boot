package de.codecentric.spring.boot.chaos.monkey.component;

/**
 * @author Benjamin Wilms
 */
public enum MetricType {

    CONTROLLER("controller"), RESTCONTROLLER("restController"), REPOSITORY("repository"), COMPONENT("component"), SERVICE("service"),
    LATENCY_ASSAULT("assault.latency"), EXCEPTION_ASSAULT("assault.exception"),KILLAPP_ASSAULT("assault.killapp"), APPLICATION_REQ_COUNT
            ("application.request.count");

    private String metricName;

    MetricType(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        String metricBaseName = "chaos.monkey.";
        return metricBaseName + metricName;
    }


}
