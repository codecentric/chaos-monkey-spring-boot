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

import static de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateWatcher.ErrorResponse.ERROR_BODY;
import static de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateWatcher.ErrorResponse.ERROR_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoRestTemplateService;
import java.util.Optional;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

class ChaosMonkeyRestTemplateWatcherIntegrationTest {

    @SpringBootTest(properties = {"chaos.monkey.watcher.rest-template=true"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class WatcherIntegrationTest {

        @Autowired
        private RestTemplate restTemplate;

        @Test
        public void testInterceptorIsPresent() {
            Optional<ClientHttpRequestInterceptor> result = this.restTemplate.getInterceptors().stream()
                    .filter(interceptor -> (interceptor instanceof ChaosMonkeyRestTemplateWatcher)).findFirst();
            assertThat(result).isPresent();
        }
    }

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-template=true",
            "chaos.monkey.assaults.exceptions-active=true"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class ExceptionAssaultIntegrationTest {

        @Autowired
        private DemoRestTemplateService demoRestTemplateService;

        @Test
        public void testRestTemplateExceptionAssault() {
            assertThatThrownBy(() -> this.demoRestTemplateService.callWithRestTemplate())
                    .hasMessage(500 + " " + ERROR_TEXT + ": \"" + ERROR_BODY + '"');
        }
    }

    @Slf4j
    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-template=true", "chaos.monkey.assaults.latency-active=true",
            "chaos.monkey.test.rest-template.time-out=20"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    static class LatencyAssaultIntegrationTest {

        @Autowired
        private DemoRestTemplateService demoRestTemplateService;

        @Test
        public void testRestTemplateLatencyAssault() {
            assertThatThrownBy(() -> this.demoRestTemplateService.callWithRestTemplate()).hasCauseInstanceOf(SSLException.class).hasMessage(
                    "I/O error on GET request for \"https://www.codecentric.de\": Read timed out; nested exception is javax.net.ssl.SSLException: Read timed out");
        }
    }
}
