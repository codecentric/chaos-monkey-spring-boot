/*
 * Copyright 2021-2025 the original author or authors.
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

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoWebClientService;
import io.netty.handler.timeout.ReadTimeoutException;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChaosMonkeyWebClientWatcherIntegrationTest {

    @Nested
    @SpringBootTest(properties = {"chaos.monkey.watcher.web-client=true", "chaos.monkey.enabled=true", "chaos.monkey.assaults.exceptions-active=true",
            "chaos.monkey.assaults.exception.type=org.springframework.web.reactive.function.client.WebClientResponseException",
            "chaos.monkey.assaults.exception.method=create", "chaos.monkey.assaults.exception.arguments[0].type=int",
            "chaos.monkey.assaults.exception.arguments[0].value=500", "chaos.monkey.assaults.exception.arguments[1].type=java.lang.String",
            "chaos.monkey.assaults.exception.arguments[1].value=Failed",
            "chaos.monkey.assaults.exception.arguments[2].type=org.springframework.http.HttpHeaders",
            "chaos.monkey.assaults.exception.arguments[2].value=null", "chaos.monkey.assaults.exception.arguments[3].type=byte[]",
            "chaos.monkey.assaults.exception.arguments[3].value=[70,97,105,108]", // "Fail" in UTF8
            "chaos.monkey.assaults.exception.arguments[4].type=java.nio.charset.Charset",
            "chaos.monkey.assaults.exception.arguments[4].value=null",}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    class ExceptionAssaultIntegrationTest {

        @Autowired
        private DemoWebClientService demoWebClientService;

        @Test
        public void testWebClientExceptionAssault() {

            assertThatThrownBy(() -> this.demoWebClientService.callWithWebClient()).isInstanceOf(WebClientResponseException.class)
                    .has(new Condition<>((ex) -> ((WebClientResponseException) ex).getResponseBodyAsString().equals("Fail"), "Body equals fail"));
        }
    }

    @Nested
    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.web-client=true", "chaos.monkey.assaults.latency-active=true",
            "chaos.monkey.test.rest-template.time-out=20"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    class LatencyAssaultIntegrationTest {

        @Autowired
        private DemoWebClientService demoWebClientService;

        @Test
        public void testWebClientLatencyAssault() {
            assertThatThrownBy(() -> this.demoWebClientService.callWithWebClient()).hasCauseInstanceOf(ReadTimeoutException.class);
        }
    }
}
