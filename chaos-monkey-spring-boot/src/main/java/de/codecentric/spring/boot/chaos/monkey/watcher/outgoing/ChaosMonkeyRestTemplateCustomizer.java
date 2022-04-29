/*
 * Copyright 2021-2022 the original author or authors.
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

import java.util.List;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/** @author Marcel Becker */
public class ChaosMonkeyRestTemplateCustomizer implements RestTemplateCustomizer {

    private final ChaosMonkeyRestTemplateWatcher interceptor;

    public ChaosMonkeyRestTemplateCustomizer(ChaosMonkeyRestTemplateWatcher interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        final List<ClientHttpRequestInterceptor> existingInterceptors = restTemplate.getInterceptors();
        if (!existingInterceptors.contains(interceptor)) {
            restTemplate.getInterceptors().add(interceptor);
        }
    }
}
