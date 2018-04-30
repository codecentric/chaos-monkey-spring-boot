package de.codecentric.spring.boot.chaos.monkey.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Benjamin Wilms
 */


@RunWith(MockitoJUnitRunner.class)
public class ChaosMonkeyControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ChaosMonkeySettings settings;

    private final String baseUrl = "/chaosmonkey";

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        ChaosMonkeyProperties monkeyProperties = getMonkeyProperties(false);
        AssaultProperties assaultProperties = getAssaultProperties();
        WatcherProperties watcherProperties = getWatcherProperties();
        settings = getChaosMonkeySettings(monkeyProperties, assaultProperties, watcherProperties);

        // default
        mockMvc = MockMvcBuilders.standaloneSetup(new ChaosMonkeyController(settings)).build();

    }

    // STATUS
    @Test
    public void chaosMonkeyIsEnabled() throws Exception {
        settings.getChaosMonkeyProperties().setEnabled(true);
        mockMvc = MockMvcBuilders.standaloneSetup(new ChaosMonkeyController(settings)).build();


        this.mockMvc.perform(get(baseUrl + "/status")).andExpect(status().isOk())
                .andExpect(content().string(is("Ready to be evil!")));
    }

    @Test
    public void chaosMonkeyIsDisabled() throws Exception {
        settings.getChaosMonkeyProperties().setEnabled(false);
        mockMvc = MockMvcBuilders.standaloneSetup(new ChaosMonkeyController(settings)).build();

        mockMvc.perform(get(baseUrl + "/status")).andExpect(status().isServiceUnavailable())
                .andExpect(content().string(is("You switched me off!")));
    }

    // CONFIGURATION GET
    @Test
    public void chaosMonkeyGetConfiguration() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/configuration")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(settings)));
    }

    // CONFIGURATION POST
    @Test
    public void chaosMonkeyPostConfiguration() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/configuration").content(objectMapper.writeValueAsString(settings)).contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                .isOk())
                .andExpect(content().string(is("Chaos Monkey config has changed")));
    }

    @Test
    public void chaosMonkeyPostConfigurationBadCase() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/configuration").content(objectMapper.writeValueAsString(settings)).contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status()
                        .is4xxClientError());
    }

    @Test
    public void chaosMonkeyPostConfigurationBadCaseEmptyWatcher() throws Exception {
        settings.setWatcherProperties(null);

        mockMvc = MockMvcBuilders.standaloneSetup(new ChaosMonkeyController(settings)).build();

        this.mockMvc.perform(post(baseUrl + "/configuration").content(objectMapper.writeValueAsString(settings)).contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                .is4xxClientError());
    }

    // ENABLE POST
    @Test
    public void chaosMonkeyPostEnable() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/enable"))
                .andExpect(status()
                        .isOk())
                .andExpect(content().string(is("Chaos Monkey is enabled")));
    }

    // DISABLE POST
    @Test
    public void chaosMonkeyPostDisable() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/disable"))
                .andExpect(status()
                        .isOk())
                .andExpect(content().string(is("Chaos Monkey is disabled")));
    }

    // ASSAULTS GET
    @Test
    public void chaosMonkeyGetAssaults() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/configuration/assaults")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(settings.getAssaultProperties())));
    }

    // ASSAULTS POST
    @Test
    public void chaosMonkeyPostAssaults() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/configuration/assaults").content(objectMapper.writeValueAsString(settings.getAssaultProperties())).contentType(MediaType
                .APPLICATION_JSON)).andExpect(status()
                .isOk())
                .andExpect(content().string(is("Assault config has changed")));
    }

    @Test
    public void chaosMonkeyPostAssaultsBadCase() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/configuration/assaults").content(objectMapper.writeValueAsString("not valid")).contentType(MediaType
                .APPLICATION_JSON)).andExpect(status()
                .is4xxClientError());
    }

    // POST WATCHER
    @Test
    public void chaosMonkeyPostWatcher() throws Exception {
        this.mockMvc.perform(post(baseUrl + "/configuration/watcher").content(objectMapper.writeValueAsString(settings.getWatcherProperties()))
                .contentType(MediaType
                        .APPLICATION_JSON)).andExpect(status()
                .isMethodNotAllowed())
        ;
    }

    // Watcher GET
    @Test
    public void chaosMonkeyGetWatcher() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/configuration/watcher")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(settings.getWatcherProperties())));
    }


    private ChaosMonkeySettings getChaosMonkeySettings(ChaosMonkeyProperties monkeyProperties, AssaultProperties assaultProperties, WatcherProperties watcherProperties) {
        return new ChaosMonkeySettings(monkeyProperties, assaultProperties, watcherProperties);
    }

    private WatcherProperties getWatcherProperties() {
        WatcherProperties props = new WatcherProperties();
        props.setController(true);
        props.setRepository(true);
        props.setRestController(true);
        props.setService(true);
        props.setComponent(true);

        return props;
    }

    private AssaultProperties getAssaultProperties() {
        AssaultProperties props = new AssaultProperties();
        props.setRestartApplicationActive(true);
        props.setLatencyActive(true);
        props.setRestartApplicationActive(true);
        props.setLevel(1);
        props.setExceptionsActive(true);
        props.setLatencyRangeEnd(100);
        props.setLatencyRangeStart(10);

        return props;
    }

    private ChaosMonkeyProperties getMonkeyProperties(boolean status) {
        ChaosMonkeyProperties properties = new ChaosMonkeyProperties();
        properties.setEnabled(true);
        return properties;
    }

}