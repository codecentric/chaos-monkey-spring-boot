package de.codecentric.spring.boot.chaos.monkey.web.client;

import de.codecentric.spring.boot.chaos.monkey.configuration.WebClientAssaultProperties;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ChaosMonkeyRestTemplateInterceptor implements ClientHttpRequestInterceptor {

  private final List<RestTemplateAssault> restTemplateAssaults;
  private final WebClientAssaultProperties webClientAssaultProperties;

  public ChaosMonkeyRestTemplateInterceptor(final List<RestTemplateAssault> restTemplateAssaults,
      WebClientAssaultProperties webClientAssaultProperties) {
    this.restTemplateAssaults = restTemplateAssaults;
    this.webClientAssaultProperties = webClientAssaultProperties;
  }

  @Override
  public ClientHttpResponse intercept(final HttpRequest httpRequest, byte[] bytes,
      ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    final ClientHttpResponse response;
    if (webClientAssaultProperties.getRestTemplate().getEnabled() && !restTemplateAssaults
        .isEmpty()) {
      response = selectAssaultAndProcess(httpRequest, clientHttpRequestExecution);
    } else {
      response = clientHttpRequestExecution.execute(httpRequest, bytes);
    }
    return response;
  }

  private ClientHttpResponse selectAssaultAndProcess(final HttpRequest httpRequest,
      ClientHttpRequestExecution clientHttpRequestExecution) {
    return this.restTemplateAssaults
        .get(new Random().nextInt(restTemplateAssaults.size()))
        .process(httpRequest, clientHttpRequestExecution);

  }


}
