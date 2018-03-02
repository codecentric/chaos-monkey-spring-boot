package de.mrbwilms.spring.boot.chaos.monkey;

import de.mrbwilms.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.mrbwilms.spring.boot.chaos.monkey.demo.ChaosDemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test-default-profile.properties")
public class ChaosDemoApplicationDefaultProfileTest {

    @Autowired(required = false)
    private ChaosMonkey chaosMonkey;

    @Autowired
    private Environment env;

    @Test
    public void contextLoads() {

        assertNull(chaosMonkey);
    }

    @Test
    public void checkEnvironment() throws Exception {
        assertThat(env.getProperty("chaos.monkey.attack.controller"),is("true"));
    }
}