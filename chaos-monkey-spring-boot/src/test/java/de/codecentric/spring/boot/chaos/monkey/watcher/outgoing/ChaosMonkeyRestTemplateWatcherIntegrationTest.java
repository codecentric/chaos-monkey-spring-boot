package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import static de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateWatcher.ErrorResponse.ERROR_BODY;
import static de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyRestTemplateWatcher.ErrorResponse.ERROR_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoRestTemplateService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class ChaosMonkeyRestTemplateWatcherIntegrationTest {

  @SpringBootTest(
      properties = {"chaos.monkey.watcher.rest-template=true"},
      classes = {ChaosDemoApplication.class})
  @ActiveProfiles("chaos-monkey")
  static class WatcherIntegrationTest {

    @Autowired private RestTemplate restTemplate;

    @Test
    public void testInterceptorIsPresent() {
      Optional<ClientHttpRequestInterceptor> result =
          this.restTemplate.getInterceptors().stream()
              .filter(interceptor -> (interceptor instanceof ChaosMonkeyRestTemplateWatcher))
              .findFirst();
      assertThat(result).isPresent();
    }
  }

  @SpringBootTest(
      properties = {
        "chaos.monkey.enabled=true",
        "chaos.monkey.watcher.rest-template=true",
        "chaos.monkey.assaults.exceptions-active=true"
      },
      classes = {ChaosDemoApplication.class})
  @ActiveProfiles("chaos-monkey")
  static class ExceptionAssaultIntegrationTest {

    @Autowired private DemoRestTemplateService demoRestTemplateService;

    @Test
    public void testRestTemplateExceptionAssault() {
      try {
        this.demoRestTemplateService.callWithRestTemplate();
        fail("No HttpClientErrorException occurred!");
      } catch (HttpStatusCodeException ex) {
        String message = ex.getMessage();
        assertThat(message)
            .isEqualTo(ex.getRawStatusCode() + " " + ERROR_TEXT + ": [" + ERROR_BODY + "]");
      }
    }
  }

  @Slf4j
  @SpringBootTest(
      properties = {
        "chaos.monkey.enabled=true",
        "chaos.monkey.watcher.rest-template=true",
        "chaos.monkey.assaults.latency-active=true",
        "chaos.monkey.test.rest-template.time-out=20"
      },
      classes = {ChaosDemoApplication.class})
  @ActiveProfiles("chaos-monkey")
  static class LatencyAssaultIntegrationTest {

    @Autowired private DemoRestTemplateService demoRestTemplateService;

    @Test
    public void testRestTemplateLatencyAssault() {
      try {
        this.demoRestTemplateService.callWithRestTemplate();
        fail("No ResourceAccessException occurred!");
      } catch (ResourceAccessException ex) {
        log.debug("exception caught, everything is fine!", ex);
      }
    }
  }
}
