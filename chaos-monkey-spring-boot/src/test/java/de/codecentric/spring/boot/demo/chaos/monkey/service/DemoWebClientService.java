package de.codecentric.spring.boot.demo.chaos.monkey.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class DemoWebClientService {

  private final WebClient webClient;

  public DemoWebClientService(WebClient webClient) {
    this.webClient = webClient;
  }

  public String callWithWebClient() {
    return this.webClient
        .method(HttpMethod.GET)
        .uri("https://www.codecentric.de")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
}
