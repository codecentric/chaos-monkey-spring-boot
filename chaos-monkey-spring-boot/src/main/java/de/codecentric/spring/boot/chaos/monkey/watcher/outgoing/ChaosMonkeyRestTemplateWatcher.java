/*
 * Copyright 2021-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author Marcel Becker
 */
public class ChaosMonkeyRestTemplateWatcher implements ClientHttpRequestInterceptor {

    private final WatcherProperties watcherProperties;
    private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;

    public ChaosMonkeyRestTemplateWatcher(final ChaosMonkeyRequestScope chaosMonkeyRequestScope, final WatcherProperties watcherProperties) {
        this.chaosMonkeyRequestScope = chaosMonkeyRequestScope;
        this.watcherProperties = watcherProperties;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution)
            throws IOException {
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        if (watcherProperties.isRestTemplate()) {
            chaosMonkeyRequestScope.callChaosMonkey(ChaosTarget.REST_TEMPLATE, httpRequest.getURI().toString());
        }
        return response;
    }
}
