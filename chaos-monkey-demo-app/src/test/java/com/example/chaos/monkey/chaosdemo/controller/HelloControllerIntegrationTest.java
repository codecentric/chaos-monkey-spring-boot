package com.example.chaos.monkey.chaosdemo.controller;

import com.example.chaos.monkey.chaosdemo.ChaosDemoApplication;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
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

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class HelloControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private HelloController helloController;


    @Test
    public void contextLoads() {
        assertThat(helloController, notNullValue());
    }

    @Test
    public void checkHelloEndpoint() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + this.serverPort + "/hello", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello!", response.getBody());
    }

}