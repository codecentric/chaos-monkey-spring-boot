package de.codecentric.spring.boot.chaos.monkey.web.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

public interface RestTemplateAssault {

  ClientHttpResponse process(HttpRequest httpRequest,
      ClientHttpRequestExecution clientHttpRequestExecution);

}
