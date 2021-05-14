package de.codecentric.spring.boot.chaos.monkey.web.client;

import static de.codecentric.spring.boot.chaos.monkey.web.client.RestTemplateErrorResponseAssault.ErrorResponse.ERROR_BODY;
import static de.codecentric.spring.boot.chaos.monkey.web.client.RestTemplateErrorResponseAssault.ErrorResponse.ERROR_TEXT;
import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoRestTemplateService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
    properties = {
        "chaos.monkey.web.client.enabled=true",
        "chaos.monkey.web.client.rest-template.enabled=true",
        "chaos.monkey.web.client.rest-template.error-response=true"
    },
    classes = {ChaosDemoApplication.class}
)
@ActiveProfiles("chaos-monkey")
class ChaosMonkeyRestTemplateInterceptorIntegrationTest {

  @Autowired
  private DemoRestTemplateService demoRestTemplateService;
  @Autowired
  private RestTemplate restTemplate;

  @Test
  public void testInterceptorIsPresent() {
    Optional<ClientHttpRequestInterceptor> result = this.restTemplate
        .getInterceptors().stream().filter(
            interceptor ->
                (interceptor instanceof ChaosMonkeyRestTemplateInterceptor)
        ).findFirst();
    assertThat(result).isPresent();
  }

  @Test
  public void testRestTemplateAssault() {
    try {
      this.demoRestTemplateService
          .callWithRestTemplate();
    } catch (HttpClientErrorException ex) {
      String message = ex.getMessage();
      assertThat(message)
          .isEqualTo(ex.getRawStatusCode() + " " + ERROR_TEXT + ": [" + ERROR_BODY + "]");
    }
  }

}