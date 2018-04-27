package com.example.chaos.monkey.chaosdemo.controller;

import com.example.chaos.monkey.chaosdemo.ChaosDemoApplication;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Benjamin Wilms
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ChaosMonkeySettings chaosMonkeySettings;


    @Test
    public void expectLongRunningGetRequest() {
        chaosMonkeySettings.getAssaultProperties().setLatencyRangeStart(1000);
        chaosMonkeySettings.getAssaultProperties().setLatencyRangeEnd(1000);

    }

    private ResponseEntity<ChaosMonkeyProperties> postResponseEntity(ChaosMonkeyProperties chaosMonkeyProperties) {
        return this.testRestTemplate.postForEntity("http://localhost:" + this.serverPort + "/chaos-monkey/configuration",
                chaosMonkeyProperties, ChaosMonkeyProperties.class);
    }
}