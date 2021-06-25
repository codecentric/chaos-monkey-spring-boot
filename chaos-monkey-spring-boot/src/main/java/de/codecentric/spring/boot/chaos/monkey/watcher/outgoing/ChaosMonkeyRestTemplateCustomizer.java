package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import java.util.List;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/** @author Marcel Becker */
public class ChaosMonkeyRestTemplateCustomizer implements RestTemplateCustomizer {

  private final ChaosMonkeyRestTemplateWatcher interceptor;

  public ChaosMonkeyRestTemplateCustomizer(ChaosMonkeyRestTemplateWatcher interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void customize(RestTemplate restTemplate) {
    final List<ClientHttpRequestInterceptor> existingInterceptors = restTemplate.getInterceptors();
    if (!existingInterceptors.contains(interceptor)) {
      restTemplate.getInterceptors().add(interceptor);
    }
  }
}
