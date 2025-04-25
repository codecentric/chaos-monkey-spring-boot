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
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoRestTemplateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ChaosMonkeyRestTemplateWatcherIntegrationMultipleInvocationTest {


    private static final Logger log = LoggerFactory.getLogger(ChaosMonkeyRestTemplateWatcherIntegrationMultipleInvocationTest.class);

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-template=true",
            "chaos.monkey.assaults.deterministic=true","chaos.monkey.assaults.level=2",
            "chaos.monkey.assaults.exceptions-active=true",
            "chaos.monkey.assaults.exception.type=org.springframework.web.client.HttpServerErrorException",
            "chaos.monkey.assaults.exception.arguments[0].type=org.springframework.http.HttpStatusCode",
            "chaos.monkey.assaults.exception.arguments[0].value=500"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class ExceptionAssaultIntegrationTest {

        @Autowired
        private DemoRestTemplateService demoRestTemplateService;

        @Test
        public void testRestTemplateExceptionAssault() {
            for (int invocationCount = 0; invocationCount < 6 ; invocationCount++) {
                log.info("InvocationCount# {}",invocationCount);
                if(invocationCount % 2 == 0) {
                    assertThatNoException().isThrownBy(() ->demoRestTemplateService.callWithRestTemplate());
                 }else{
                    assertThatThrownBy(() -> demoRestTemplateService.callWithRestTemplate()).hasMessage("500 INTERNAL_SERVER_ERROR");
                }

            }

        }
    }
}
