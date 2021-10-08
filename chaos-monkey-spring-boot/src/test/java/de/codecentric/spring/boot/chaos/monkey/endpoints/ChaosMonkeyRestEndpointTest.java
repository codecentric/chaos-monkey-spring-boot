package de.codecentric.spring.boot.chaos.monkey.endpoints;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyScheduler;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.chaos.monkey.endpoints.dto.WatcherPropertiesUpdate;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("chaos-monkey")
@ContextConfiguration(classes = {ChaosDemoApplication.class})
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
      "management.endpoint.chaosmonkey.enabled=true",
      "management.endpoints.web.exposure.include=chaosmonkey",
      "management.endpoints.enabled-by-default=true"
    })
public class ChaosMonkeyRestEndpointTest {

  @Autowired private TestRestTemplate testRestTemplate;
  @MockBean private ChaosMonkeySettings chaosMonkeySettings;
  @MockBean private ChaosMonkeyScheduler chaosMonkeyScheduler;

  @Test
  public void testWatcherPropertiesUpdateApplied() {
    final WatcherPropertiesUpdate watcherPropertiesUpdate = new WatcherPropertiesUpdate();
    watcherPropertiesUpdate.setController(Boolean.TRUE);
    watcherPropertiesUpdate.setRestController(Boolean.TRUE);
    watcherPropertiesUpdate.setComponent(Boolean.TRUE);
    watcherPropertiesUpdate.setService(Boolean.TRUE);
    watcherPropertiesUpdate.setRepository(Boolean.TRUE);
    watcherPropertiesUpdate.setRestTemplate(Boolean.TRUE);
    watcherPropertiesUpdate.setWebClient(Boolean.TRUE);
    watcherPropertiesUpdate.setActuatorHealth(Boolean.TRUE);

    final WatcherProperties watcherProperties = new WatcherProperties();

    when(chaosMonkeySettings.getWatcherProperties()).thenReturn(watcherProperties);

    ResponseEntity<String> response =
        testRestTemplate.postForEntity(
            "/actuator/chaosmonkey/watchers", watcherPropertiesUpdate, String.class);

    BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    BDDAssertions.then(watcherProperties.isController()).isTrue();
    BDDAssertions.then(watcherProperties.isRestController()).isTrue();
    BDDAssertions.then(watcherProperties.isComponent()).isTrue();
    BDDAssertions.then(watcherProperties.isService()).isTrue();
    BDDAssertions.then(watcherProperties.isRepository()).isTrue();
    BDDAssertions.then(watcherProperties.isRestTemplate()).isTrue();
    BDDAssertions.then(watcherProperties.isWebClient()).isTrue();
    BDDAssertions.then(watcherProperties.isActuatorHealth()).isTrue();

    verify(chaosMonkeyScheduler, times(1)).reloadConfig();
  }
}
