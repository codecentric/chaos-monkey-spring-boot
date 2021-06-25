package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import de.codecentric.spring.boot.chaos.monkey.watcher.outgoing.ChaosMonkeyWebClientWatcher.ErrorClientResponse;
import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoWebClientService;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;

class ChaosMonkeyWebClientWatcherIntegrationTest {

  @SpringBootTest(
      properties = {
        "chaos.monkey.watcher.web-client=true",
        "chaos.monkey.enabled=true",
        "chaos.monkey.assaults.exceptions-active=true"
      },
      classes = {ChaosDemoApplication.class})
  @ActiveProfiles("chaos-monkey")
  static class ExceptionAssaultIntegrationTest {

    @Autowired private DemoWebClientService demoWebClientService;

    @Test
    public void testWebClientExceptionAssault() {
      try {
        this.demoWebClientService.callWithWebClient();
        fail("No WebClientResponseException occurred!");
      } catch (WebClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        assertThat(body).isEqualTo(ErrorClientResponse.ERROR_BODY);
      }
    }
  }

  @Slf4j
  @SpringBootTest(
      properties = {
        "chaos.monkey.enabled=true",
        "chaos.monkey.watcher.web-client=true",
        "chaos.monkey.assaults.latency-active=true",
        "chaos.monkey.test.rest-template.time-out=20"
      },
      classes = {ChaosDemoApplication.class})
  @ActiveProfiles("chaos-monkey")
  static class LatencyAssaultIntegrationTest {

    @Autowired private DemoWebClientService demoWebClientService;

    @Test
    public void testWebClientLatencyAssault() {
      assertThatThrownBy(() -> this.demoWebClientService.callWithWebClient())
          .hasCauseInstanceOf(ReadTimeoutException.class);
    }
  }
}
