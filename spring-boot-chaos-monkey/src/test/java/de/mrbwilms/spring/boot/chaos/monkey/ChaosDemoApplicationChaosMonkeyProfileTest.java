package de.mrbwilms.spring.boot.chaos.monkey;

import de.mrbwilms.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.mrbwilms.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.mrbwilms.spring.boot.chaos.monkey.demo.ChaosDemoApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChaosDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"chaos.monkey" +
        ".watcher.controller=true","chaos.monkey.assaults.level=1","chaos.monkey.assaults.latencyRangeStart=1000", "chaos.monkey.assaults" +
        ".killApplicationActive=true","spring.profiles" +
        ".active=chaos-monkey"})
public class ChaosDemoApplicationChaosMonkeyProfileTest {

    @Autowired
    private ChaosMonkey chaosMonkey;

    @Autowired
    private ChaosMonkeySettings monkeySettings;

    @Autowired
    private Environment env;

    @Before
    public void setUp() {
        chaosMonkey = new ChaosMonkey(monkeySettings.getAssaultProperties());
    }

    @Test
    public void contextLoads() {

        assertNotNull(chaosMonkey);
    }


    @Test
    public void checkChaosSettingsObject() {
        assertNotNull(monkeySettings);
    }

    @Test
    public void checkChaosSettingsValues() {
        assertThat(monkeySettings.getAssaultProperties().getLatencyRangeEnd(), is(15000));
        assertThat(monkeySettings.getAssaultProperties().getLatencyRangeStart(), is(1000));
        assertThat(monkeySettings.getAssaultProperties().getLevel(), is(1));
        assertThat(monkeySettings.getAssaultProperties().isLatencyActive(), is(false));
        assertThat(monkeySettings.getAssaultProperties().isExceptionsActive(), is(false));
        assertThat(monkeySettings.getAssaultProperties().isKillApplicationActive(), is(true));
        assertThat(monkeySettings.getWatcherProperties().isController(), is(true));
        assertThat(monkeySettings.getWatcherProperties().isRepository(), is(false));
        assertThat(monkeySettings.getWatcherProperties().isRestController(), is(false));
        assertThat(monkeySettings.getWatcherProperties().isService(), is(false));
    }
}