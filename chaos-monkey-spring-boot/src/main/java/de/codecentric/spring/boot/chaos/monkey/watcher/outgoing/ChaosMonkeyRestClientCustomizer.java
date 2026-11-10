package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.web.client.RestClient;

public class ChaosMonkeyRestClientCustomizer implements RestClientCustomizer {

    private final ChaosMonkeyRestClientWatcher interceptor;

    public ChaosMonkeyRestClientCustomizer(ChaosMonkeyRestClientWatcher interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.requestInterceptors(interceptors -> {
            if (!interceptors.contains(interceptor)) {
                interceptors.add(interceptor);
            }
        });
    }
}