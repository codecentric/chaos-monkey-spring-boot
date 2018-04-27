package de.codecentric.spring.boot.chaos.monkey.controller;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.demo.ChaosDemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Test
    public void shouldDisableChaosMonkeyExecution() {
        ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
        chaosMonkeyProperties.setEnabled(false);

        then(postResponseEntity(chaosMonkeyProperties).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertEquals(false, this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled());
    }

    @Test
    public void shouldEnableChaosMonkeyExecution() {
        ChaosMonkeyProperties chaosMonkeyProperties = new ChaosMonkeyProperties();
        chaosMonkeyProperties.setEnabled(true);

        then(postResponseEntity(chaosMonkeyProperties).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertEquals(true, this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled());
    }

    private ResponseEntity<ChaosMonkeyProperties> postResponseEntity(ChaosMonkeyProperties chaosMonkeyProperties) {
        return this.testRestTemplate.postForEntity("http://localhost:" + this.serverPort + "/chaos-monkey/configuration",
                chaosMonkeyProperties, ChaosMonkeyProperties.class);
    }
}