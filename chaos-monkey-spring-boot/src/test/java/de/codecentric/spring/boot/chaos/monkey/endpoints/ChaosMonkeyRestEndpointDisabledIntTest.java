package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.Before;
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

import static org.junit.Assert.*;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test-chaos-monkey-endpoints-disabled.properties")
public class ChaosMonkeyRestEndpointDisabledIntTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        baseUrl = "http://localhost:" + this.serverPort + "/actuator/chaosmonkey";
    }

    @Test
    public void getConfiguration() {
        ResponseEntity<ChaosMonkeySettings> chaosMonkeySettingsResult =
                testRestTemplate.getForEntity(baseUrl , ChaosMonkeySettings.class);

        assertEquals(HttpStatus.NOT_FOUND, chaosMonkeySettingsResult.getStatusCode());
    }

}