package de.codecentric.spring.boot.demo.chaos.monkey.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoRestTemplateService {

  private final RestTemplate restTemplate;

  public DemoRestTemplateService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ResponseEntity<String> callWithRestTemplate() {
    return this.restTemplate.getForEntity("https://www.codecentric.de", String.class);
  }
}
