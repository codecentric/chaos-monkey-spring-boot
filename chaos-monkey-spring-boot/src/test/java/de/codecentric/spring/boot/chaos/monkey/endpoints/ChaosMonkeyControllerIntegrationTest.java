package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
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
public class ChaosMonkeyControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ChaosMonkeySettings chaosMonkeySettings;

    @Autowired
    private TestRestTemplate testRestTemplate;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        baseUrl = "http://localhost:" + this.serverPort + "/chaosmonkey";
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
                testRestTemplate.getForEntity(baseUrl + "/configuration", ChaosMonkeySettings.class);

        assertEquals(HttpStatus.OK, chaosMonkeySettingsResult.getStatusCode());
        assertEquals(chaosMonkeySettings, chaosMonkeySettingsResult.getBody());
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
                testRestTemplate.getForEntity(baseUrl + "/configuration/watcher", WatcherProperties.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(chaosMonkeySettings.getWatcherProperties(), result.getBody());
    }

    // Assault Tests
    @Test
    public void getAssaultConfiguration() {
        ResponseEntity<AssaultProperties> result =
                testRestTemplate.getForEntity(baseUrl + "/configuration/assaults", AssaultProperties.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(chaosMonkeySettings.getAssaultProperties(), result.getBody());
    }


    private ResponseEntity<String> postChaosMonkeySettings(ChaosMonkeySettings chaosMonkeySettings) {

        return this.testRestTemplate.postForEntity(baseUrl + "/configuration",
                chaosMonkeySettings, String.class);
    }

    private ResponseEntity<String> postHttpEntity(HttpEntity value) {

        return this.testRestTemplate.postForEntity(baseUrl + "/configuration",
                value, String.class);
    }
}