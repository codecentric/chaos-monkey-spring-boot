package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ChaosMonkeyRestClientWatcher implements ClientHttpRequestInterceptor {

    private final WatcherProperties watcherProperties;
    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    public ChaosMonkeyRestClientWatcher(final ChaosMonkeyRequestScope chaosMonkeyRequestScope, final WatcherProperties watcherProperties) {
        this.chaosMonkeyRequestScope = chaosMonkeyRequestScope;
        this.watcherProperties = watcherProperties;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution)
            throws IOException {
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        if (watcherProperties.isRestClient()) {
            chaosMonkeyRequestScope.callChaosMonkey(ChaosTarget.REST_CLIENT, httpRequest.getURI().toString());
        }
        return response;
    }
}