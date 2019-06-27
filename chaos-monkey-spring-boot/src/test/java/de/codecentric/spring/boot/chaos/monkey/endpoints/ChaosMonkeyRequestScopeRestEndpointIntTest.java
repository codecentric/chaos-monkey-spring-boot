/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.*;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test-chaos-monkey-profile.properties")
public class ChaosMonkeyRequestScopeRestEndpointIntTest {

    @LocalServerPort
    private int serverPort;


    @Autowired
    private ChaosMonkeySettings chaosMonkeySettings;

    @Autowired
    private TestRestTemplate testRestTemplate;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        baseUrl = "http://localhost:" + this.serverPort + "/actuator/chaosmonkey";
    }

    @Test
    public void disableChaosMonkeyExecutionNotAllowed() {
        ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
        chaosMonkeyProperties.setEnabled(false);
        chaosMonkeySettings.setChaosMonkeyProperties(chaosMonkeyProperties);

        then(postChaosMonkeySettings(chaosMonkeySettings).getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);

        assertEquals(false, this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled());
    }


    @Test
    public void getConfiguration() {
        ResponseEntity<ChaosMonkeySettings> chaosMonkeySettingsResult =
                testRestTemplate.getForEntity(baseUrl, ChaosMonkeySettings.class);

        assertEquals(HttpStatus.OK, chaosMonkeySettingsResult.getStatusCode());
        assertEquals(chaosMonkeySettings.toString(), chaosMonkeySettingsResult.getBody().toString());
    }

    @Test
    public void postChaosMonkeySettingsEqualsNULL() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChaosMonkeySettings> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = postHttpEntity(entity);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
    }

    @Test
    public void postChaosMonkeySettingsValueObjectAssaultPropertiesNULL() {
        ResponseEntity<String> responseEntity = postChaosMonkeySettings(new ChaosMonkeySettings(new ChaosMonkeyProperties(), null,
                new WatcherProperties()));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
    }

    @Test
    public void postChaosMonkeySettingsValueObjectWatcherPropertiesNULL() {
        ResponseEntity<String> responseEntity = postChaosMonkeySettings(new ChaosMonkeySettings(new ChaosMonkeyProperties(), new AssaultProperties(),
                null));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
    }

    // Watcher Tests
    @Test
    public void getWatcherConfiguration() {
        ResponseEntity<WatcherProperties> result =
                testRestTemplate.getForEntity(baseUrl + "/watchers", WatcherProperties.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(chaosMonkeySettings.getWatcherProperties().toString(), result.getBody().toString());
    }

    @Test
    public void postWatcherConfigurationGoodCase() {

        WatcherPropertiesUpdate watcherProperties = new WatcherPropertiesUpdate();
        watcherProperties.setService(true);

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/watchers", watcherProperties, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Watcher config has changed", result.getBody());
    }

    // Assault Tests
    @Test
    public void getAssaultConfiguration() {
        ResponseEntity<AssaultProperties> result =
                testRestTemplate.getForEntity(baseUrl + "/assaults", AssaultProperties.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(chaosMonkeySettings.getAssaultProperties().toString(), result.getBody().toString());
    }

    @Test
    public void postAssaultConfigurationGoodCase() {
        AssaultPropertiesUpdate assaultProperties = new AssaultPropertiesUpdate();
        assaultProperties.setLevel(10);
        assaultProperties.setLatencyRangeEnd(100);
        assaultProperties.setLatencyRangeStart(200);
        assaultProperties.setLatencyActive(true);
        assaultProperties.setExceptionsActive(false);
        assaultProperties.setException(new AssaultException());

        // Do not set memory properties - optional :)
        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Assault config has changed", result.getBody());
    }


    @Test
    public void postMinimalUpdate() {
        @Data class MinimalSubmission {
            private int level = 10;
        }
        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", new MinimalSubmission(), String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Assault config has changed", result.getBody());
    }

    @Test
    public void postAssaultConfigurationBadCaseLevelEmpty() {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLatencyRangeEnd(100);
        assaultProperties.setLatencyRangeStart(200);
        assaultProperties.setLatencyActive(true);

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void postAssaultConfigurationBadCaseLatencyRangeEndEmpty() {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLevel(1000);
        assaultProperties.setLatencyRangeStart(200);
        assaultProperties.setLatencyActive(true);

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void postAssaultConfigurationBadCaseLatencyRangeStartEmpty() {
        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLevel(1000);
        assaultProperties.setLatencyRangeEnd(200);
        assaultProperties.setLatencyActive(true);

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void postAssaultConfigurationBadCaseInvalidExceptionType() {
        AssaultException exception = new AssaultException();
        exception.setType("SomeInvalidException");

        AssaultProperties assaultProperties = new AssaultProperties();
        assaultProperties.setLevel(10);
        assaultProperties.setLatencyRangeEnd(100);
        assaultProperties.setLatencyRangeStart(200);
        assaultProperties.setLatencyActive(true);
        assaultProperties.setExceptionsActive(false);
        assaultProperties.setException(exception);

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/assaults", assaultProperties, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    // STATUS
    @Test
    public void getStatusIsEnabled() {
        chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);

        ResponseEntity<String> result =
                testRestTemplate.getForEntity(baseUrl + "/status", String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Ready to be evil!", result.getBody());
    }

    @Test
    public void getStatusIsDisabled() {
        chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);

        ResponseEntity<String> result =
                testRestTemplate.getForEntity(baseUrl + "/status", String.class);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
        assertEquals("You switched me off!", result.getBody());
    }

    // ENABLE CHAOS MONKEY

    @Test
    public void postToEnableChaosMonkey() {

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/enable", null, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Chaos Monkey is enabled", result.getBody());
    }

    // DISABLE CHAOS MONKEY
    @Test
    public void postToDisableChaosMonkey() {

        ResponseEntity<String> result =
                testRestTemplate.postForEntity(baseUrl + "/disable", null, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Chaos Monkey is disabled", result.getBody());
    }

    private ResponseEntity<String> postChaosMonkeySettings(ChaosMonkeySettings chaosMonkeySettings) {

        return this.testRestTemplate.postForEntity(baseUrl,
                chaosMonkeySettings, String.class);
    }

    private ResponseEntity<String> postHttpEntity(HttpEntity value) {

        return this.testRestTemplate.postForEntity(baseUrl,
                value, String.class);
    }
}
